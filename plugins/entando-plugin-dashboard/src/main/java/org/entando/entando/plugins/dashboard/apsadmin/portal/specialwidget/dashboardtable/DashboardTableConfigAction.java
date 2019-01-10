/*
 *
 * <Your licensing text here>
 *
 */
package org.entando.entando.plugins.dashboard.apsadmin.portal.specialwidget.dashboardtable;

import com.agiletec.apsadmin.portal.specialwidget.SimpleWidgetConfigAction;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.plugins.dashboard.aps.system.services.dashboardtable.IDashboardTableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DashboardTableConfigAction extends SimpleWidgetConfigAction {

    private static final Logger _logger = LoggerFactory.getLogger(DashboardTableConfigAction.class);

    protected String extractInitConfig() {
        String result = super.extractInitConfig();
        String id = this.getWidget().getConfig().getProperty("id");
        if (StringUtils.isNotBlank(id)) {
            this.setId(new Integer(id));
        }
        return result;
    }

    public List<Integer> getDashboardTablesId() {
        try {
            List<Integer> dashboardTables = this.getDashboardTableManager().searchDashboardTables(null);
            return dashboardTables;
        } catch (Throwable t) {
            _logger.error("error in getDashboardTablesId", t);
            throw new RuntimeException("Error getting dashboardTables list", t);
        }
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    protected IDashboardTableManager getDashboardTableManager() {
        return _dashboardTableManager;
    }

    public void setDashboardTableManager(IDashboardTableManager dashboardTableManager) {
        this._dashboardTableManager = dashboardTableManager;
    }

    private int _id;
    private IDashboardTableManager _dashboardTableManager;
}
