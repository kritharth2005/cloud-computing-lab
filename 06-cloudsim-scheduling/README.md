# Program 6: Simulate a Cloud Scenario Using CloudSim with a New Scheduling Algorithm

## Aim

To simulate a cloud scenario using CloudSim and run a scheduling algorithm that is not present in CloudSim.

## Algorithm implemented: Shortest Job First (SJF)

CloudSim's default `DatacenterBroker` sends cloudlets (tasks) in the order they are submitted (**FCFS**) and assigns them to VMs **round-robin**. It has no Shortest Job First scheduler.

The custom `SJFBroker` in this program extends `DatacenterBroker` and overrides `submitCloudlets()`:

1. Sort all cloudlets by length (million instructions), shortest first.
2. For each cloudlet in that order, bind it to the VM that will become free earliest (tracked as `vmReadyTime`).
3. Hand the bound cloudlets to the parent class, which sends them to the datacenter.

VMs use `CloudletSchedulerSpaceShared`, so each VM runs one cloudlet at a time and the order of execution matters. The program runs the same workload (2 VMs, 8 cloudlets) with both brokers and prints the results for comparison.

## Requirements

- Java (JDK 8 or later)
- Eclipse IDE
- CloudSim 3.0.3: download `cloudsim-3.0.3.zip` from <https://github.com/Cloudslab/cloudsim/releases/tag/cloudsim-3.0.3> and unzip it

## Procedure

1. Download and unzip **CloudSim 3.0.3**. The library is at `cloudsim-3.0.3/jars/cloudsim-3.0.3.jar`.
2. Open **Eclipse** and create a new Java project: **File → New → Java Project**, name it `CloudSimSJF`, and click **Finish**. If Eclipse asks to create `module-info.java`, click **Don't Create**.
3. Add CloudSim to the project: right-click the project → **Build Path → Configure Build Path → Libraries → Classpath → Add External JARs**, select `cloudsim-3.0.3.jar`, and click **Apply and Close**.
4. Right-click `src` → **New → Class**, name it `SJFScheduling` (leave the package empty), and paste the code from [`src/SJFScheduling.java`](src/SJFScheduling.java).
5. The program follows the standard CloudSim steps:
   1. Initialise CloudSim: `CloudSim.init(numUsers, calendar, traceFlag)`
   2. Create a datacenter (host with PEs, RAM, bandwidth, storage) using `DatacenterCharacteristics` and `VmAllocationPolicySimple`
   3. Create a broker: here either the default `DatacenterBroker` or the custom `SJFBroker`
   4. Create VMs and submit them: `broker.submitVmList(vmList)`
   5. Create cloudlets and submit them: `broker.submitCloudletList(cloudletList)`
   6. Start the simulation: `CloudSim.startSimulation()`, then print results
6. Run it: right-click `SJFScheduling.java` → **Run As → Java Application**.

**Without Eclipse** (from the folder containing this README):

```bash
javac -cp /path/to/cloudsim-3.0.3.jar -d out src/SJFScheduling.java
java  -cp /path/to/cloudsim-3.0.3.jar:out SJFScheduling        # on Windows use ; instead of :
```

## Program

See [`src/SJFScheduling.java`](src/SJFScheduling.java). The scheduling algorithm:

```java
static class SJFBroker extends DatacenterBroker {

    @Override
    protected void submitCloudlets() {
        List<Cloudlet> cloudlets = getCloudletList();
        List<Vm> vms = getVmsCreatedList();

        // Step 1: shortest job first.
        Collections.sort(cloudlets, new Comparator<Cloudlet>() {
            public int compare(Cloudlet a, Cloudlet b) {
                return Long.compare(a.getCloudletLength(), b.getCloudletLength());
            }
        });

        // Step 2: bind each job to the VM that becomes free earliest.
        double[] vmReadyTime = new double[vms.size()];
        for (Cloudlet cloudlet : cloudlets) {
            int best = 0;
            for (int i = 1; i < vms.size(); i++) {
                if (vmReadyTime[i] < vmReadyTime[best]) best = i;
            }
            Vm vm = vms.get(best);
            cloudlet.setVmId(vm.getId());
            vmReadyTime[best] += (double) cloudlet.getCloudletLength() / vm.getMips();
        }

        // Step 3: the parent class sends the bound cloudlets in list order.
        super.submitCloudlets();
    }
}
```

## Output

```
=== Default CloudSim broker (FCFS + round robin) ===
Cloudlet   Status   VM     Length   Start      Finish     Waiting
1          Success  1      5000     0.10       5.10       0.00
3          Success  1      10000    5.10       15.10      5.00
5          Success  1      2000     15.10      17.10      15.00
7          Success  1      8000     17.10      25.10      17.00
0          Success  0      40000    0.10       40.10      0.00
2          Success  0      30000    40.10      70.10      40.00
4          Success  0      25000    70.10      95.10      70.00
6          Success  0      15000    95.10      110.10     95.00
Average waiting time    : 30.25
Average completion time : 47.22
Makespan                : 110.10

=== Custom SJF broker (not in CloudSim) ===
Cloudlet   Status   VM     Length   Start      Finish     Waiting
5          Success  0      2000     0.10       2.10       0.00
1          Success  1      5000     0.10       5.10       0.00
7          Success  0      8000     2.10       10.10      2.00
3          Success  1      10000    5.10       15.10      5.00
6          Success  0      15000    10.10      25.10      10.00
4          Success  1      25000    15.10      40.10      15.00
2          Success  0      30000    25.10      55.10      25.00
0          Success  1      40000    40.10      80.10      40.00
Average waiting time    : 12.12
Average completion time : 29.10
Makespan                : 80.10
```

With the default broker, all the long jobs land on VM 0 and the short ones wait behind them. SJF runs short jobs first and balances the load across both VMs, cutting average waiting time from 30.25 to 12.12 and makespan from 110.10 to 80.10.

## Result

A cloud scenario was simulated in CloudSim, and a Shortest Job First scheduling algorithm (not present in CloudSim) was implemented as a custom broker. It reduced average waiting time and makespan compared to CloudSim's default scheduling.
