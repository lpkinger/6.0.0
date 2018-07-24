Ext.QuickTips.init();
Ext.define('erp.controller.pm.atp.ATPOperateDetail', {
    extend: 'Ext.app.Controller',
    views:[
     		'pm.atp.ATPOpDetailGridPanel1','pm.atp.ATPOpDetailGridPanel2','pm.atp.ATPOpDetailGridPanel3',
     		'pm.atp.ATPOpDetailGridPanel4','pm.atp.ATPOpDetailGridPanel5','pm.atp.ATPOpDetailGridPanel6',
     		'pm.atp.ATPOperateDetail','pm.atp.ATPOpDetailForm','pm.mps.Toolbar',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField'
     	  
     	],
    init:function(){
    	var me = this;
    }
});