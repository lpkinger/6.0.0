Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.Findings', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	views : ['core.form.Panel', 'fs.loaded.Findings','core.form.MultiField','core.form.CheckBoxGroup',
	         'core.button.Save','core.button.Modify','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'],
	init : function() {
		var me = this;
		this.control({
    		'field[name=li_filevel]': {
				afterrender : function(f) {
    				Ext.create('Ext.tip.ToolTip', {
				        target: 'li_filevel',
				        maxWidth : 800,
				        width : 800,
				        html:'<div>只要有一项非正常项即触发一级预警，在触发一级预警时，客户经理前往调查发现情况属实且异动情况在我司不可接受的范围（风险管理部负责人认定），则相继触发二至五级预警。</div><div>一级预警：客户经理前往企业核实情况；</div><div>二级预警：冻结授信额度；</div><div>三级预警：要求卖方企业回购应收账款；</div><div>四级预警：提起诉讼;</div><div>五级预警：催收或呆账</div>'
				    });
				}
    		},
			'erpSaveButton': {
				afterrender:function(btn){
					var status = Ext.getCmp('li_statuscode');
					if(status&&status.value!='ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){		
    				me.FormUtil.onUpdate(this);
    			}
        	}
		})
	}
});