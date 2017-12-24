package za.co.rin.chartool.charts.generators;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import za.co.rin.chartool.charts.config.ChartDefinition;
import za.co.rin.chartool.charts.datasource.ChartDataSource;
import za.co.rin.chartool.charts.datasource.KeyValueDataItem;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class LineChartScriptGeneratorTest {

    private LineChartScriptGenerator lineChartScriptGenerator = new LineChartScriptGenerator();

    private JUnit4Mockery context = new JUnit4Mockery();
    private ChartColorManager chartColorManagerMock;
    private ChartDataSource chartDataSourceMock;

    private static final String TEST_COLORS = "'rgba(0, 0, 139, 0.5)', 'rgba(54, 162, 235, 0.5)'";

    @Before
    public void setUp() {
        chartColorManagerMock = context.mock(ChartColorManager.class);
        chartDataSourceMock = context.mock(ChartDataSource.class);

        lineChartScriptGenerator.setChartColorManager(chartColorManagerMock);
        lineChartScriptGenerator.setChartDataSource(chartDataSourceMock);
    }

    @Test
    public void testGetChartScript() throws Exception {
        ChartDefinition testChartDefinition = getTestChartDefinition();
        List<KeyValueDataItem> testDataItems = getTestDataItems();

        context.checking(new Expectations() {{
            oneOf(chartDataSourceMock).getKeyValueDataItems(testChartDefinition);
            will(returnValue(testDataItems));
            oneOf(chartColorManagerMock).getChartColorsJson(1);
            will(returnValue(TEST_COLORS));

        }});

        String chartScript = lineChartScriptGenerator.getChartScript(testChartDefinition);
        assertThat(chartScript, containsString("function load_test_chart()"));
        assertThat(chartScript, containsString("type: 'line',"));
        assertThat(chartScript, containsString("label: 'Test Chart'"));
        assertThat(chartScript, containsString("data: [1,2]"));
        assertThat(chartScript, containsString("backgroundColor: [" + TEST_COLORS + "]"));
        assertThat(chartScript, containsString(" document.getElementById(\"test\")"));
        assertThat(chartScript, containsString("text: 'Test Chart',"));

    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    private ChartDefinition getTestChartDefinition() {
        ChartDefinition chartDefinition = new ChartDefinition();
        chartDefinition.setId("test");
        chartDefinition.setName("Test Chart");
        chartDefinition.setDescription("Test Chart Description");

        return chartDefinition;
    }

    private List<KeyValueDataItem> getTestDataItems() {
        List<KeyValueDataItem> dataItems = new ArrayList<>();

        dataItems.add(new KeyValueDataItem("One", 1));
        dataItems.add(new KeyValueDataItem("Two", 2));

        return dataItems;
    }
}