Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.TurnRegister', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.form.Panel', 'scm.purchase.TurnRegister', 'core.grid.Panel2', 'core.toolbar.Toolbar', 
            'core.form.MultiField', 'core.button.Save', 'core.button.Add', 'core.button.Submit', 
            'core.button.Print', 'core.button.PrintHK', 'core.button.PrintEn','core.button.Upload', 'core.button.ResAudit', 
            'core.button.Audit', 'core.button.Close', 'core.button.Delete', 'core.button.Update', 'core.button.B2B',
            'core.button.DeleteDetail', 'core.button.ResSubmit', 'core.button.End','core.button.Printyestax', 'core.button.Printnotax','core.button.AttendDataCom',
            'core.button.ResEnd', 'core.button.GetPrice', 'core.button.Export', 'core.button.StandardPrice',
            'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger', 'core.form.YnField', 
            'core.grid.YnColumn', 'core.form.StatusField', 'core.form.FileField', 'core.button.PrintByCondition',
            'core.button.CopyAll', 'core.button.ResetSync', 'core.button.RefreshSync','core.button.RefreshQty','core.button.Split','core.button.TurnBankRegister2'],
    init: function() {
        this.control(
           	);
    }
});