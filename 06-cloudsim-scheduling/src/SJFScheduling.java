import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * Program 6: a cloud scenario in CloudSim with a scheduling algorithm that
 * CloudSim does not ship with: Shortest Job First (SJF).
 *
 * CloudSim's default DatacenterBroker sends cloudlets in the order they were
 * submitted (FCFS) and assigns them to VMs round-robin. SJFBroker below
 * replaces that: it sorts cloudlets by length (shortest first) and sends each
 * one to the VM that will become free earliest.
 *
 * The same workload is run twice, once with each broker, so the results can be
 * compared side by side.
 */
public class SJFScheduling {

    private static final int VM_COUNT = 2;
    private static final int VM_MIPS = 1000;
    /** Cloudlet lengths in million instructions, in submission order. */
    private static final long[] CLOUDLET_LENGTHS =
            {40000, 5000, 30000, 10000, 25000, 2000, 15000, 8000};

    /** Shortest Job First broker: the custom scheduling algorithm. */
    static class SJFBroker extends DatacenterBroker {

        SJFBroker(String name) throws Exception {
            super(name);
        }

        @Override
        protected void submitCloudlets() {
            List<Cloudlet> cloudlets = getCloudletList();
            List<Vm> vms = getVmsCreatedList();

            // Step 1: shortest job first.
            Collections.sort(cloudlets, new Comparator<Cloudlet>() {
                @Override
                public int compare(Cloudlet a, Cloudlet b) {
                    return Long.compare(a.getCloudletLength(), b.getCloudletLength());
                }
            });

            // Step 2: bind each job to the VM that becomes free earliest.
            double[] vmReadyTime = new double[vms.size()];
            for (Cloudlet cloudlet : cloudlets) {
                int best = 0;
                for (int i = 1; i < vms.size(); i++) {
                    if (vmReadyTime[i] < vmReadyTime[best]) {
                        best = i;
                    }
                }
                Vm vm = vms.get(best);
                cloudlet.setVmId(vm.getId());
                vmReadyTime[best] += (double) cloudlet.getCloudletLength() / vm.getMips();
            }

            // Step 3: the parent class sends the bound cloudlets in list order.
            super.submitCloudlets();
        }
    }

    public static void main(String[] args) throws Exception {
        Log.disable(); // hide CloudSim's per-event log so the result tables stay readable

        System.out.println("=== Default CloudSim broker (FCFS + round robin) ===");
        List<Cloudlet> fcfs = runSimulation(false);
        printResults(fcfs);

        System.out.println();
        System.out.println("=== Custom SJF broker (not in CloudSim) ===");
        List<Cloudlet> sjf = runSimulation(true);
        printResults(sjf);
    }

    private static List<Cloudlet> runSimulation(boolean useSjf) throws Exception {
        CloudSim.init(1, Calendar.getInstance(), false);

        createDatacenter("Datacenter_0");
        DatacenterBroker broker = useSjf ? new SJFBroker("SJFBroker") : new DatacenterBroker("DefaultBroker");
        int brokerId = broker.getId();

        List<Vm> vms = new ArrayList<Vm>();
        for (int id = 0; id < VM_COUNT; id++) {
            // id, owner, mips, PEs, RAM (MB), bandwidth, image size (MB), VMM, scheduler
            vms.add(new Vm(id, brokerId, VM_MIPS, 1, 512, 1000, 10000, "Xen",
                    new CloudletSchedulerSpaceShared()));
        }
        broker.submitVmList(vms);

        List<Cloudlet> cloudlets = new ArrayList<Cloudlet>();
        UtilizationModel full = new UtilizationModelFull();
        for (int id = 0; id < CLOUDLET_LENGTHS.length; id++) {
            // id, length, PEs, input file size, output file size, CPU/RAM/BW utilization
            Cloudlet cloudlet = new Cloudlet(id, CLOUDLET_LENGTHS[id], 1, 300, 300, full, full, full);
            cloudlet.setUserId(brokerId);
            cloudlets.add(cloudlet);
        }
        broker.submitCloudletList(cloudlets);

        CloudSim.startSimulation();
        CloudSim.stopSimulation();
        return broker.getCloudletReceivedList();
    }

    private static void createDatacenter(String name) throws Exception {
        List<Pe> pes = new ArrayList<Pe>();
        for (int i = 0; i < VM_COUNT; i++) {
            pes.add(new Pe(i, new PeProvisionerSimple(VM_MIPS)));
        }

        List<Host> hosts = new ArrayList<Host>();
        hosts.add(new Host(0, new RamProvisionerSimple(4096), new BwProvisionerSimple(10000),
                1000000, pes, new VmSchedulerTimeShared(pes)));

        // arch, OS, VMM, hosts, time zone, cost per sec, per MB RAM, per MB storage, per MB bandwidth
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hosts, 10.0, 3.0, 0.05, 0.001, 0.0);

        new Datacenter(name, characteristics, new VmAllocationPolicySimple(hosts),
                new LinkedList<Storage>(), 0);
    }

    private static void printResults(List<Cloudlet> cloudlets) {
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.printf("%-10s %-8s %-6s %-8s %-10s %-10s %-10s%n",
                "Cloudlet", "Status", "VM", "Length", "Start", "Finish", "Waiting");

        double totalFinish = 0;
        double totalWaiting = 0;
        double makespan = 0;
        for (Cloudlet c : cloudlets) {
            double waiting = c.getExecStartTime() - c.getSubmissionTime();
            System.out.printf("%-10d %-8s %-6d %-8d %-10s %-10s %-10s%n",
                    c.getCloudletId(), c.getCloudletStatusString(), c.getVmId(), c.getCloudletLength(),
                    df.format(c.getExecStartTime()), df.format(c.getFinishTime()), df.format(waiting));
            totalFinish += c.getFinishTime();
            totalWaiting += waiting;
            makespan = Math.max(makespan, c.getFinishTime());
        }

        int n = cloudlets.size();
        System.out.println("Average waiting time    : " + df.format(totalWaiting / n));
        System.out.println("Average completion time : " + df.format(totalFinish / n));
        System.out.println("Makespan                : " + df.format(makespan));
    }
}
