package org.cloudbus.cloudsim.examples.container;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerBwProvisionerSimple;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerPe;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerRamProvisionerSimple;
import org.cloudbus.cloudsim.container.containerProvisioners.CotainerPeProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmBwProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmPe;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmPeProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmRamProvisionerSimple;
import org.cloudbus.cloudsim.container.core.*;
import org.cloudbus.cloudsim.container.hostSelectionPolicies.HostSelectionPolicy;
import org.cloudbus.cloudsim.container.hostSelectionPolicies.HostSelectionPolicyFirstFit;
import org.cloudbus.cloudsim.container.resourceAllocatorMigrationEnabled.PowerContainerVmAllocationPolicyMigrationAbstractHostSelection;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerVmAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.PowerContainerAllocationPolicySimple;
import org.cloudbus.cloudsim.container.schedulers.ContainerCloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.container.schedulers.ContainerSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.container.schedulers.ContainerVmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.container.utils.IDs;
import org.cloudbus.cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicy;
import org.cloudbus.cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicyMaximumUsage;
import org.cloudbus.cloudsim.examples.CloudletRequestDistribution.BaseRequestDistribution;
//import org.cloudbus.cloudsim.examples.CloudletRequestDistribution.NormalDistribution;

//import org.cloudbus.cloudsim.core.CloudSim;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple example showing how to create a data center with one host, one VM, one container and run one cloudlet on it.
 */
public class chris_load_balance_test {

    /**
     * The cloudlet list.
     */
    private static List<ContainerCloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private static List<ContainerVm> vmList;

    /**
     * The vmlist.
     */

    private static List<Container> containerList;

    /**
     * The hostList.
     */

    private static List<ContainerHost> hostList;

    /**
     * The container datacenter broker.
     */
    private static ContainerScalabilityBroker broker;

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     */

    public static void main(String[] args) {
        Log.printLine("Starting chris_container_test...");

        try {
            /**
             * number of cloud Users
             */
            int num_user = 1;
            /**
             *  The fields of calender have been initialized with the current date and time.
             */
            Calendar calendar = Calendar.getInstance();
            /**
             * Deactivating the event tracing
             */
            boolean trace_flag = false;
            /**
             * 1- Like CloudSim the first step is initializing the CloudSim Package before creating any entities.
             *
             */


            CloudSim.init(num_user, calendar, trace_flag, 0.1);
            /**
             * 2-  Defining the container allocation Policy. This policy determines how Containers are
             * allocated to VMs in the data center.
             *
             */


            ContainerAllocationPolicy containerAllocationPolicy = new PowerContainerAllocationPolicySimple();

            /**
             * 3-  Defining the VM selection Policy. This policy determines which VMs should be selected for migration
             * when a host is identified as over-loaded.
             *
             */

            PowerContainerVmSelectionPolicy vmSelectionPolicy = new PowerContainerVmSelectionPolicyMaximumUsage();


            /**
             * 4-  Defining the host selection Policy. This policy determines which hosts should be selected as
             * migration destination.
             *
             */
            HostSelectionPolicy hostSelectionPolicy = new HostSelectionPolicyFirstFit();
            /**
             * 5- Defining the thresholds for selecting the under-utilized and over-utilized hosts.
             */

            double overUtilizationThreshold = 0.80;
            double underUtilizationThreshold = 0.70;
            /**
             * 6- The host list is created considering the number of hosts, and host types which are specified
             * in the {@link ConstantsExamples}.
             */
            hostList = new ArrayList<ContainerHost>();
            hostList = createHostList(ConstantsExamples.NUMBER_HOSTS);
            cloudletList = new ArrayList<ContainerCloudlet>();
            vmList = new ArrayList<ContainerVm>();
            /**
             * 7- The container allocation policy  which defines the allocation of VMs to containers.
             */
            ContainerVmAllocationPolicy vmAllocationPolicy = new
                    PowerContainerVmAllocationPolicyMigrationAbstractHostSelection(hostList, vmSelectionPolicy,
                    hostSelectionPolicy, overUtilizationThreshold, underUtilizationThreshold);
            /**
             * 8- The overbooking factor for allocating containers to VMs. This factor is used by the broker for the
             * allocation process.
             */
            int overBookingFactor = 80;
            broker = createBroker(overBookingFactor);
            int brokerId = broker.getId();
            Log.set_log_level(3);
            /**
             * 9- Creating the cloudlet, container and VM lists for submitting to the broker.
             */
            //cloudletList = createContainerCloudletList(brokerId, ConstantsExamples.NUMBER_CLOUDLETS);
            BaseRequestDistribution self_design_distribution = new BaseRequestDistribution(101, 10,
                    3,
                    20000, 100, 0);
            cloudletList = self_design_distribution.GetWorkloads();
            for(ContainerCloudlet cl : cloudletList){
                cl.setUserId(brokerId);
            }
            containerList = createContainerList(brokerId, ConstantsExamples.NUMBER_CONTAINERS);
            vmList = createVmList(brokerId, ConstantsExamples.NUMBER_VMS);
            Log.formatLine("------CHRIS VERIFY: CloudLet number: " + cloudletList.size() + " container number: "
                    + containerList.size() + " vm number: " + vmList.size());
            /**
             * 10- The address for logging the statistics of the VMs, containers in the data center.
             */
            String logAddress = "~/Results";

            for(int i = 0; i < hostList.size(); i++) {
                //unused
                PowerContainerDatacenterElasticity tmp = (PowerContainerDatacenterElasticity) createDatacenter("DatacenterElasticity",
                        PowerContainerDatacenterElasticity.class, hostList.subList(i, i + 1), vmAllocationPolicy, containerAllocationPolicy,
                        getExperimentName("CHRIS_LOAD_BALANCE_TEST", String.valueOf(overBookingFactor)),
                        ConstantsExamples.SCHEDULING_INTERVAL, logAddress,
                        ConstantsExamples.VM_STARTTUP_DELAY, ConstantsExamples.CONTAINER_STARTTUP_DELAY);
            }

            /**
             * 11- Submitting the cloudlet's , container's , and VM's lists to the broker.
             */
            broker.submitVmList(vmList);

            //binding the containerList to the specific vm.
            for(Container con : containerList.subList(0, containerList.size() / 2)){
                con.setVm(vmList.get(0));
            }
            for(Container con : containerList.subList(containerList.size() / 2, containerList.size())){
                con.setVm(vmList.get(1));
            }
            broker.submitContainerList(containerList);

            //binding the cloudlet to the vm.
            broker.submitContainerList(containerList.subList(0, containerList.size()));
            broker.submitCloudletList(cloudletList);//have to submit the cloudlet before the binding operation.
            for(ContainerCloudlet cl : cloudletList){
                broker.bindCloudletToVm(cl.getCloudletId(), vmList.get(0).getId());
            }


            //create workloads in datacenter2.
            BaseRequestDistribution base_distribution = new BaseRequestDistribution(101, 10,
                    3,
                    20000, 100, cloudletList.size());
            List<ContainerCloudlet> tmp = base_distribution.GetWorkloads();
            for(ContainerCloudlet cl : tmp){
                cl.setUserId(brokerId);
            }

            broker.submitCloudletList(tmp);
            for(ContainerCloudlet cl : tmp){
                cloudletList.add(cl);
                broker.bindCloudletToVm(cl.getCloudletId(), vmList.get(1).getId());
            }




            /**
             * 12- Determining the simulation termination time according to the cloudlet's workload.
             */
            CloudSim.terminateSimulation(ConstantsExamples.SIMULATION_LIMIT);
            /**
             * 13- Starting the simualtion.
             */
            CloudSim.startSimulation();
            //chris note: cloudsim clock can only be used in simulation.
            /**
             * 14- Stopping the simualtion.
             */
            CloudSim.stopSimulation();
            /**
             * 15- Printing the results when the simulation is finished.
             */
            List<ContainerCloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);

            Log.printLine("CHRIS CONTAINER TEST finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }



    /**
     * It creates a specific name for the experiment which is used for creating the Log address folder.
     */

    private static String getExperimentName(String... args) {
        StringBuilder experimentName = new StringBuilder();

        for (int i = 0; i < args.length; ++i) {
            if (!args[i].isEmpty()) {
                if (i != 0) {
                    experimentName.append("_");
                }

                experimentName.append(args[i]);
            }
        }

        return experimentName.toString();
    }

    /**
     * Creates the broker.
     *
     * @param overBookingFactor
     * @return the datacenter broker
     */
    private static ContainerScalabilityBroker createBroker(int overBookingFactor) {

        ContainerScalabilityBroker broker = null;

        try {
            Container c = new PowerContainer(IDs.pollId(Container.class),
                    -1,
                    (double) ConstantsExamples.CONTAINER_MIPS[0],
                    ConstantsExamples.CONTAINER_PES[0], ConstantsExamples.CONTAINER_RAM[0], ConstantsExamples.CONTAINER_BW, 0L, "Xen",
                    new ContainerCloudletSchedulerDynamicWorkload(ConstantsExamples.CONTAINER_MIPS[0], ConstantsExamples.CONTAINER_PES[0]), ConstantsExamples.SCHEDULING_INTERVAL);
            broker = new ContainerScalabilityBroker("Broker", overBookingFactor, c);
        } catch (Exception var2) {
            var2.printStackTrace();
            System.exit(0);
        }

        return broker;
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<ContainerCloudlet> list) {
        int size = list.size();
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("The cloulet size is:" + size);
        Cloudlet cloudlet;

        String indent = "    ";

        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
                + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);
            if (cloudlet.getCloudletStatusString() == "Success") {
                Log.print("SUCCESS");
                Log.printLine(indent + indent + cloudlet.getResourceId()
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent
                        + dft.format(cloudlet.getActualCPUTime()) + indent
                        + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent
                        + dft.format(cloudlet.getFinishTime()));
            }
        }
    }

    /**
     * Create the Virtual machines and add them to the list
     *
     * @param brokerId
     * @param containerVmsNumber
     */
    private static ArrayList<ContainerVm> createVmList(int brokerId, int containerVmsNumber) {
        ArrayList<ContainerVm> containerVms = new ArrayList<ContainerVm>();

        for (int i = 0; i < containerVmsNumber; ++i) {
            ArrayList<ContainerPe> peList = new ArrayList<ContainerPe>();
            int vmType = i / (int) Math.ceil((double) containerVmsNumber / ConstantsExamples.VM_TYPES);
            for (int j = 0; j < ConstantsExamples.VM_PES[vmType]; ++j) {
                peList.add(new ContainerPe(j,
                        new CotainerPeProvisionerSimple((double) ConstantsExamples.VM_MIPS[vmType])));
            }
            containerVms.add(new PowerContainerVm(IDs.pollId(ContainerVm.class), brokerId,
                    (double) ConstantsExamples.VM_MIPS[vmType], (float) ConstantsExamples.VM_RAM[vmType],
                    ConstantsExamples.VM_BW, ConstantsExamples.VM_SIZE, "Xen",
                    new ContainerSchedulerTimeSharedOverSubscription(peList),
                    new ContainerRamProvisionerSimple(ConstantsExamples.VM_RAM[vmType]),
                    new ContainerBwProvisionerSimple(ConstantsExamples.VM_BW),
                    peList, ConstantsExamples.SCHEDULING_INTERVAL));
        }
        return containerVms;
    }

    /**
     * Create the host list considering the specs listed in the {@link ConstantsExamples}.
     *
     * @param hostsNumber
     * @return
     */


    public static List<ContainerHost> createHostList(int hostsNumber) {
        ArrayList<ContainerHost> hostList = new ArrayList<ContainerHost>();
        for (int i = 0; i < hostsNumber; ++i) {
            int hostType = i / (int) Math.ceil((double) hostsNumber / ConstantsExamples.HOST_TYPES);
            ArrayList<ContainerVmPe> peList = new ArrayList<ContainerVmPe>();
            for (int j = 0; j < ConstantsExamples.HOST_PES[hostType]; ++j) {
                peList.add(new ContainerVmPe(j,
                        new ContainerVmPeProvisionerSimple((double) ConstantsExamples.HOST_MIPS[hostType])));
            }

            hostList.add(new PowerContainerHostUtilizationHistory(IDs.pollId(ContainerHost.class),
                    new ContainerVmRamProvisionerSimple(ConstantsExamples.HOST_RAM[hostType]),
                    new ContainerVmBwProvisionerSimple(1000000L), 1000000L, peList,
                    new ContainerVmSchedulerTimeSharedOverSubscription(peList),
                    ConstantsExamples.HOST_POWER[hostType]));
        }

        return hostList;
    }


    /**
     * Create the data center
     *
     * @param name
     * @param datacenterClass
     * @param hostList
     * @param vmAllocationPolicy
     * @param containerAllocationPolicy
     * @param experimentName
     * @param logAddress
     * @return
     * @throws Exception
     */

    public static ContainerDatacenter createDatacenter(String name, Class<? extends ContainerDatacenter> datacenterClass,
                                                       List<ContainerHost> hostList,
                                                       ContainerVmAllocationPolicy vmAllocationPolicy,
                                                       ContainerAllocationPolicy containerAllocationPolicy,
                                                       String experimentName, double schedulingInterval, String logAddress, double VMStartupDelay,
                                                       double ContainerStartupDelay) throws Exception {
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0D;
        double cost = 3.0D;
        double costPerMem = 0.05D;
        double costPerStorage = 0.001D;
        double costPerBw = 0.0D;
        ContainerDatacenterCharacteristics characteristics = new
                ContainerDatacenterCharacteristics(arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage,
                costPerBw);
        ContainerDatacenter datacenter = new PowerContainerDatacenterElasticity(name, characteristics, vmAllocationPolicy,
                containerAllocationPolicy, new LinkedList<Storage>(), schedulingInterval, experimentName, logAddress,
                VMStartupDelay, ContainerStartupDelay);

        return datacenter;
    }

    /**
     * create the containers for hosting the cloudlets and binding them together.
     *
     * @param brokerId
     * @param containersNumber
     * @return
     */

    public static List<Container> createContainerList(int brokerId, int containersNumber) {
        ArrayList<Container> containers = new ArrayList<Container>();
        for (int i = 0; i < containersNumber; ++i) {
            int containerType = i / (int) Math.ceil((double) containersNumber / ConstantsExamples.CONTAINER_TYPES);
            containers.add(new PowerContainer(IDs.pollId(Container.class),
                    brokerId,
                    (double) ConstantsExamples.CONTAINER_MIPS[containerType],
                    ConstantsExamples.CONTAINER_PES[containerType], ConstantsExamples.CONTAINER_RAM[containerType], ConstantsExamples.CONTAINER_BW, 0L, "Xen",
                    new ContainerCloudletSchedulerDynamicWorkload(ConstantsExamples.CONTAINER_MIPS[containerType], ConstantsExamples.CONTAINER_PES[containerType]), ConstantsExamples.SCHEDULING_INTERVAL));
        }
        return containers;
    }

    /**
     * Creating the cloudlet list that are going to run on containers
     *
     * @param brokerId
     * @param numberOfCloudlets
     * @return
     * @throws FileNotFoundException
     */
    public static List<ContainerCloudlet> createContainerCloudletList(int brokerId, int numberOfCloudlets)
            throws FileNotFoundException {
        String inputFolderName = ContainerCloudSimExample1.class.getClassLoader().getResource("workload/planetlab").getPath();
        ArrayList<ContainerCloudlet> cloudletList = new ArrayList<ContainerCloudlet>();
        long fileSize = 300L;
        long outputSize = 300L;
        UtilizationModelStochastic UtilizationModelStochastic = new UtilizationModelStochastic();
        java.io.File inputFolder1 = new java.io.File(inputFolderName);
        java.io.File[] files1 = inputFolder1.listFiles();
        Log.formatLine("%s， file number: %d", inputFolderName, files1.length);
        int createdCloudlets = 0;
        for (java.io.File aFiles1 : files1) {
            java.io.File inputFolder = new java.io.File(aFiles1.toString());
            java.io.File[] files = inputFolder.listFiles();
            for (int i = 0; i < files.length; ++i) {
                // Log.formatLine("----------------chris: cpu utilization file %s, cloudlet number: %d",files[i].toString(), createdCloudlets);
                if (createdCloudlets < numberOfCloudlets) {
                    ContainerCloudlet cloudlet = null;
                    try {
                        cloudlet = new ContainerCloudlet(IDs.pollId(ContainerCloudlet.class), ConstantsExamples.CLOUDLET_LENGTH, 1,
                                fileSize, outputSize,
                                new UtilizationModelPlanetLabInMemoryExtended(files[i].getAbsolutePath(), ConstantsExamples.SCHEDULING_INTERVAL),
                                //new UtilizationModelStochastic(100000),
                                UtilizationModelStochastic, UtilizationModelStochastic);

                    } catch (Exception var13) {
                        var13.printStackTrace();
                        System.exit(0);
                    }
                    cloudlet.setExecStartTime(CloudSim.clock() + i * 500);
                    Log.formatLine("chris_note: Cloudlet id:" + cloudlet.getCloudletId() + " exec start time: " + cloudlet.getExecStartTime());
                    cloudlet.setUserId(brokerId);
                    cloudletList.add(cloudlet);
                    createdCloudlets += 1;
                } else {

                    return cloudletList;
                }
            }

        }
        return cloudletList;
    }

}
