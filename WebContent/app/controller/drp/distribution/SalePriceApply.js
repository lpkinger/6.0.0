Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.SalePriceApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.form.Panel', 'drp.distribution.SalePriceApply', 'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.form.MultiField', 'core.button.Save', 'core.button.Add', 'core.button.Submit', 'core.button.Print', 'core.button.Upload', 'core.button.ResAudit', 'core.button.Audit', 'core.button.Close', 'core.button.Delete', 'core.button.Update', 'core.button.DeleteDetail', 'core.button.ResSubmit', 'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger', 'core.button.Scan', 'core.button.TurnSale'],
    init: function() {
        var me = this;
        this.control({
        });
    },
    onGridItemClick: function(selModel, record) {
        this.GridUtil.onGridItemClick(selModel, record)
    },
    getForm: function(btn) {
        return btn.ownerCt.ownerCt
    }
});