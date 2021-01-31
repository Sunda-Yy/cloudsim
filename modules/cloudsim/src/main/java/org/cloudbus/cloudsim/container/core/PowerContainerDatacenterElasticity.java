package org.cloudbus.cloudsim.container.core;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.container.lists.ContainerList;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerVmAllocationPolicy;
import org.cloudbus.cloudsim.container.utils.CostumeCSVWriter;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.predicates.PredicateType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




public class PowerContainerDatacenterElasticity extends PowerContainerDatacenterCM{
    private double vmStartupDelay;
    private double containerStartupDelay;
    private int last_broker_Id;

    public PowerContainerDatacenterElasticity(String name, ContainerDatacenterCharacteristics characteristics,
                                      ContainerVmAllocationPolicy vmAllocationPolicy,
                                      ContainerAllocationPolicy containerAllocationPolicy, List<Storage> storageList,
                                      double schedulingInterval, String experimentName, String logAddress,
                                      double vmStartupDelay, double containerStartupDelay) throws Exception {
        super(name, characteristics, vmAllocationPolicy, containerAllocationPolicy, storageList, schedulingInterval, experimentName, logAddress,
                vmStartupDelay, containerStartupDelay);
        this.containerStartupDelay = containerStartupDelay;
        this.vmStartupDelay = vmStartupDelay;
    }


//    @Override
//    public void processEvent(SimEvent ev) {
//        int srcId = -1;
//
//        switch (ev.getTag()) {
//            // Resource characteristics inquiry
//            case CloudSimTags.RESOURCE_CHARACTERISTICS:
//                srcId = ((Integer) ev.getData()).intValue();
//                sendNow(srcId, ev.getTag(), getCharacteristics());
//                break;
//
//            // Resource dynamic info inquiry
//            case CloudSimTags.RESOURCE_DYNAMICS:
//                srcId = ((Integer) ev.getData()).intValue();
//                sendNow(srcId, ev.getTag(), 0);
//                break;
//
//            case CloudSimTags.RESOURCE_NUM_PE:
//                srcId = ((Integer) ev.getData()).intValue();
//                int numPE = getCharacteristics().getNumberOfPes();
//                sendNow(srcId, ev.getTag(), numPE);
//                break;
//
//            case CloudSimTags.RESOURCE_NUM_FREE_PE:
//                srcId = ((Integer) ev.getData()).intValue();
//                int freePesNumber = getCharacteristics().getNumberOfFreePes();
//                sendNow(srcId, ev.getTag(), freePesNumber);
//                break;
//
//            // New Cloudlet arrives
//            case CloudSimTags.CLOUDLET_SUBMIT:
//                processCloudletSubmit(ev, false);
//                break;
//
//            // New Cloudlet arrives, but the sender asks for an ack
//            case CloudSimTags.CLOUDLET_SUBMIT_ACK:
//                processCloudletSubmit(ev, true);
//                break;
//
//            // Cancels a previously submitted Cloudlet
//            case CloudSimTags.CLOUDLET_CANCEL:
//                processCloudlet(ev, CloudSimTags.CLOUDLET_CANCEL);
//                break;
//
//            // Pauses a previously submitted Cloudlet
//            case CloudSimTags.CLOUDLET_PAUSE:
//                processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE);
//                break;
//
//            // Pauses a previously submitted Cloudlet, but the sender
//            // asks for an acknowledgement
//            case CloudSimTags.CLOUDLET_PAUSE_ACK:
//                processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE_ACK);
//                break;
//
//            // Resumes a previously submitted Cloudlet
//            case CloudSimTags.CLOUDLET_RESUME:
//                processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME);
//                break;
//
//            // Resumes a previously submitted Cloudlet, but the sender
//            // asks for an acknowledgement
//            case CloudSimTags.CLOUDLET_RESUME_ACK:
//                processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME_ACK);
//                break;
//
//            // Moves a previously submitted Cloudlet to a different resource
//            case CloudSimTags.CLOUDLET_MOVE:
//                processCloudletMove((int[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE);
//                break;
//
//            // Moves a previously submitted Cloudlet to a different resource
//            case CloudSimTags.CLOUDLET_MOVE_ACK:
//                processCloudletMove((int[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE_ACK);
//                break;
//
//            // Checks the status of a Cloudlet
//            case CloudSimTags.CLOUDLET_STATUS:
//                processCloudletStatus(ev);
//                break;
//
//            // Ping packet
//            case CloudSimTags.INFOPKT_SUBMIT:
//                processPingRequest(ev);
//                break;
//
//            case CloudSimTags.VM_CREATE:
//                processVmCreate(ev, false);
//                break;
//
//            case CloudSimTags.VM_CREATE_ACK:
//                processVmCreate(ev, true);
//                break;
//
//            case CloudSimTags.VM_DESTROY:
//                processVmDestroy(ev, false);
//                break;
//
//            case CloudSimTags.VM_DESTROY_ACK:
//                processVmDestroy(ev, true);
//                break;
//
//            case CloudSimTags.VM_MIGRATE:
//                processVmMigrate(ev, false);
//                break;
//
//            case CloudSimTags.VM_MIGRATE_ACK:
//                processVmMigrate(ev, true);
//                break;
//
//            case CloudSimTags.VM_DATA_ADD:
//                processDataAdd(ev, false);
//                break;
//
//            case CloudSimTags.VM_DATA_ADD_ACK:
//                processDataAdd(ev, true);
//                break;
//
//            case CloudSimTags.VM_DATA_DEL:
//                processDataDelete(ev, false);
//                break;
//
//            case CloudSimTags.VM_DATA_DEL_ACK:
//                processDataDelete(ev, true);
//                break;
//
//            case CloudSimTags.VM_DATACENTER_EVENT:
//                updateCloudletProcessing();
//                checkCloudletCompletion();
//                break;
//            case containerCloudSimTags.CONTAINER_SUBMIT:
//                processContainerSubmit(ev, true);
//                break;
//
//            case containerCloudSimTags.CONTAINER_MIGRATE:
//                processContainerMigrate(ev, false);
//                // other unknown tags are processed by this method
//                break;
//
//            default:
//                processOtherEvent(ev);
//                break;
//        }
//    }

    protected void updateCloudletProcessing() {

        // Log.printLine("Power data center is Updating the cloudlet processing");
        if (getCloudletSubmitted() == -1 || getCloudletSubmitted() == CloudSim.clock()) {
            CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_DATACENTER_EVENT));
            schedule(getId(), getSchedulingInterval(), CloudSimTags.VM_DATACENTER_EVENT);
            return;
        }
        double currentTime = CloudSim.clock();

        // if some time passed since last processing
        if (currentTime > getLastProcessTime()) {
            Log.formatLine(3, "FUNC: updateCloudProcessing: current time, " + currentTime
                    + " source broker: " + last_broker_Id);
            //chris add: send a message that contains the mean utilization of this datacenter.
//            ArrayList<Double> container_utilization= new ArrayList<Double>();
//            for(Container c : getContainerList()){
//                container_utilization.add((double)c.getAvailablePesNum());
//            }
            sendNow(last_broker_Id, containerCloudSimTags.LOAD_BALANCE_SCHEDULE, getContainerList());



            double minTime = updateCloudetProcessingWithoutSchedulingFutureEventsForce();

            if (!isDisableMigrations()) {
                List<Map<String, Object>> migrationMap = getVmAllocationPolicy().optimizeAllocation(
                        getContainerVmList());
                int previousContainerMigrationCount = getContainerMigrationCount();
                int previousVmMigrationCount = getVmMigrationCount();
                if (migrationMap != null) {
                    List<ContainerVm> vmList = new ArrayList<ContainerVm>();
                    for (Map<String, Object> migrate : migrationMap) {
                        if (migrate.containsKey("container")) {
                            Container container = (Container) migrate.get("container");
                            ContainerVm targetVm = (ContainerVm) migrate.get("vm");
                            ContainerVm oldVm = container.getVm();
                            if (oldVm == null) {
                                Log.formatLine(
                                        "%.2f: Migration of Container #%d to Vm #%d is started",
                                        currentTime,
                                        container.getId(),
                                        targetVm.getId());
                            } else {
                                Log.formatLine(
                                        "%.2f: Migration of Container #%d from Vm #%d to VM #%d is started",
                                        currentTime,
                                        container.getId(),
                                        oldVm.getId(),
                                        targetVm.getId());
                            }
                            incrementContainerMigrationCount();
                            targetVm.addMigratingInContainer(container);

                            if (migrate.containsKey("NewEventRequired")) {
                                if (!vmList.contains(targetVm)) {
                                    // A new VM is created  send a vm create request with delay :)
//                                Send a request to create Vm after 100 second
//                                            create a new event for this. or overright the vm create
                                    Log.formatLine(
                                            "%.2f: Migration of Container #%d to newly created Vm #%d is started",
                                            currentTime,
                                            container.getId(),
                                            targetVm.getId());
                                    targetVm.containerDestroyAll();
                                    send(
                                            getId(),
                                            vmStartupDelay,
                                            CloudSimTags.VM_CREATE,
                                            migrate);
                                    vmList.add(targetVm);

                                    send(
                                            getId(),
                                            containerStartupDelay + vmStartupDelay
                                            , containerCloudSimTags.CONTAINER_MIGRATE,
                                            migrate);

                                } else {
                                    Log.formatLine(
                                            "%.2f: Migration of Container #%d to newly created Vm #%d is started",
                                            currentTime,
                                            container.getId(),
                                            targetVm.getId());
//                                    send a request for container migration after the vm is created
//                                    it would be 100.4
                                    send(
                                            getId(),
                                            containerStartupDelay + vmStartupDelay
                                            , containerCloudSimTags.CONTAINER_MIGRATE,
                                            migrate);
                                }


                            } else {
                                send(
                                        getId(),
                                        containerStartupDelay,
                                        containerCloudSimTags.CONTAINER_MIGRATE,
                                        migrate);
                            }
                        } else {
                            ContainerVm vm = (ContainerVm) migrate.get("vm");
                            PowerContainerHost targetHost = (PowerContainerHost) migrate.get("host");
                            PowerContainerHost oldHost = (PowerContainerHost) vm.getHost();

                            if (oldHost == null) {
                                Log.formatLine(
                                        "%.2f: Migration of VM #%d to Host #%d is started",
                                        currentTime,
                                        vm.getId(),
                                        targetHost.getId());
                            } else {
                                Log.formatLine(
                                        "%.2f: Migration of VM #%d from Host #%d to Host #%d is started",
                                        currentTime,
                                        vm.getId(),
                                        oldHost.getId(),
                                        targetHost.getId());
                            }

                            targetHost.addMigratingInContainerVm(vm);
                            incrementMigrationCount();

                            /** VM migration delay = RAM / bandwidth **/
                            // we use BW / 2 to model BW available for migration purposes, the other
                            // half of BW is for VM communication
                            // around 16 seconds for 1024 MB using 1 Gbit/s network
                            send(
                                    getId(),
                                    vm.getRam() / ((double) targetHost.getBw() / (2 * 8000)),
                                    CloudSimTags.VM_MIGRATE,
                                    migrate);
                        }


                    }


                    migrationMap.clear();
                    vmList.clear();
                }
                getContainerMigrationList().add((double) (getContainerMigrationCount() - previousContainerMigrationCount));

                Log.formatLine(CloudSim.clock() +  ": The Number Container of Migrations is:  "
                        + (getContainerMigrationCount() - previousContainerMigrationCount));
                Log.formatLine(CloudSim.clock() + ": The Number of VM Migrations is:  "
                        +  (getVmMigrationCount() - previousVmMigrationCount));
                String[] vmMig = {Double.toString(CloudSim.clock()), Integer.toString(getVmMigrationCount() - previousVmMigrationCount)};                   // <--declared statement
                String[] msg = {Double.toString(CloudSim.clock()), Integer.toString(getContainerMigrationCount() - previousContainerMigrationCount)};                   // <--declared statement
                try {
                    getContainerMigrationWriter().writeTofile(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    getVmMigrationWriter().writeTofile(vmMig);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                int numberOfNewVms = getNewlyCreatedVms();
                getNewlyCreatedVmsList().add(numberOfNewVms);
                String[] msg1 = {Double.toString(CloudSim.clock()), Integer.toString(numberOfNewVms)};                   // <--declared statement
                try {
                    getNewlyCreatedVmWriter().writeTofile(msg1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // schedules an event to the next time
            if (minTime != Double.MAX_VALUE) {
                CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_DATACENTER_EVENT));
                send(getId(), getSchedulingInterval(), CloudSimTags.VM_DATACENTER_EVENT);
            }
            setLastProcessTime(currentTime);

        }

    }



    @Override
    protected void processCloudletSubmit(SimEvent ev, boolean ack) {
//        super.processCloudletSubmit(ev, ack);
        this.last_broker_Id = ev.getSource();
        try {
            ContainerCloudlet cl = (ContainerCloudlet) ev.getData();
            // checks whether this Cloudlet has finished or not
            if (cl.isFinished()) {
                String name = CloudSim.getEntityName(cl.getUserId());
                if (ack) {
                    int[] data = new int[3];
                    data[0] = getId();
                    data[1] = cl.getCloudletId();
                    data[2] = CloudSimTags.FALSE;
                    int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
                    sendNow(cl.getUserId(), tag, data);
                }
                sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);
                return;
            }
            // process this Cloudlet to this CloudResource
            cl.setResourceParameter(getId(), getCharacteristics().getCostPerSecond(), getCharacteristics()
                    .getCostPerBw());
            //Chris tuning container:
            int containerId = cl.getContainerId();
            if(containerId < 0){
                Log.formatLine("Chris BINDING CLOUDLET: " + CloudSim.clock());
                sendNow(cl.getUserId(), containerCloudSimTags.BINDING_CLOUDLET, cl);
                return;
            }
            int vmId = cl.getVmId();
            if(vmId < 0){
                Log.formatLine("Assign the cloudlet to the located container now.");
                getContainerList();
                if(ContainerList.getById(getContainerList(),containerId) == null){
                    Log.formatLine("Container %d, has not been created and allocated.");
                    return;
                }
                cl.setVmId(ContainerList.getById(getContainerList(),containerId).getVm().getId());
                vmId = cl.getVmId();
            }
            int userId = cl.getUserId();
            Log.formatLine("chris note: cloudlet id: " + cl.getCloudletId() + " container id: " + containerId
                    + " VM id: " + cl.getVmId() +  " start time: " + cl.getExecStartTime());
            // time to transfer the files
            double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

            ContainerHost host = getVmAllocationPolicy().getHost(vmId, userId);
            ContainerVm vm = host.getContainerVm(vmId, userId);

            Container container = vm.getContainer(containerId, userId);
            double estimatedFinishTime = container.getContainerCloudletScheduler().cloudletSubmit(cl, fileTransferTime);
            Log.formatLine("chris note: cloudlet id:" + cl.getCloudletId() + "estimated finish time: " + estimatedFinishTime);


            // if this cloudlet is in the exec queue
            if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
                estimatedFinishTime += fileTransferTime;
                send(getId(), estimatedFinishTime, CloudSimTags.VM_DATACENTER_EVENT);
            }

            if (ack) {
                int[] data = new int[3];
                data[0] = getId();
                data[1] = cl.getCloudletId();
                data[2] = CloudSimTags.TRUE;

                // unique tag = operation tag
                int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
                sendNow(cl.getUserId(), tag, data);
            }
        } catch (ClassCastException c) {
            Log.printLine(String.format("%s.processCloudletSubmit(): ClassCastException error.", getName()));
            c.printStackTrace();
        } catch (Exception e) {
            Log.printLine(String.format("%s.processCloudletSubmit(): Exception error.", getName()));
            e.printStackTrace();
        }

        checkCloudletCompletion();
        setCloudletSubmitted(CloudSim.clock());
    }
}
