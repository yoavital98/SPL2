package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    Cluster cluster;
    GPU.Type type;
    int id;
    GPU gpu;
    Model model;
    Data data;
    Data.Type dataType = Data.Type.Images;
    Student student;
    Student.Degree degree;
    Model.Status modelStatus;
    Model.Results results;
    @Before
    public void setUp() throws Exception {
        type = GPU.Type.RTX2080;
        cluster = Cluster.getInstance();
        id = 1;
        gpu = new GPU(type,cluster,id);
        data = new Data(dataType, 0, 10000);
        degree = Student.Degree.MSc;
        student = new Student("Moshe", "SE", degree);
        modelStatus = Model.Status.PreTrained;
        results = Model.Results.None;
        model = new Model("model1", data, student,modelStatus, results);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void trainModel() {
        assertTrue(gpu.trainModel(model));
        assertFalse(gpu.trainModel(model));
        Model.Status status = Model.Status.Training;
        assertEquals(gpu.getStatus(), status);
        assertEquals(gpu.getData(), data);
        assertEquals(gpu.getDataBatchAmount(), 10);
    }

    @Test
    public void testModel() {
        assertTrue(gpu.testModel(model));
        Model.Status tested = Model.Status.Tested;
        assertEquals(gpu.getStatus(), tested);
    }

    @Test
    public void tick() {
        gpu.trainModel(model);
        gpu.addProcessedDataBatches(new DataBatch(data, 0));
        assertEquals(gpu.getCurrentTicks(), 2);
        assertEquals(gpu.getDataBatchAmount(), 10);
        assertEquals(gpu.getProcessedDataBatch().size(), 1);
        gpu.tick();
        assertEquals(gpu.getCurrentTicks(), 1);
        assertEquals(gpu.getDataBatchAmount(), 10);
        assertEquals(gpu.getProcessedDataBatch().size(), 1);
        gpu.tick();
        assertEquals(gpu.getCurrentTicks(), 2);
        assertEquals(gpu.getDataBatchAmount(),9);
        assertEquals(gpu.getProcessedDataBatch().size(), 0);
    }

    @Test
    public void addProcessedDataBatches() {
        assertEquals(gpu.getProcessedDataBatch().size(), 0);
        for(int i = 0; i < 16; i++) {
            assertTrue(gpu.addProcessedDataBatches(new DataBatch(data, 0)));
            assertEquals(gpu.getProcessedDataBatch().size(), i+1);
        }
        assertFalse(gpu.addProcessedDataBatches(new DataBatch(data, 0)));
        assertEquals(gpu.getProcessedDataBatch().size(), 16);
    }

    @Test
    public void finishProcess() {
        gpu.trainModel(model);
        Model.Status training = Model.Status.Training;
        Model.Status trained = Model.Status.Trained;
        assertEquals(gpu.getStatus(), training);
        gpu.finishProcess();
        assertEquals(gpu.getStatus(), trained);
    }
}