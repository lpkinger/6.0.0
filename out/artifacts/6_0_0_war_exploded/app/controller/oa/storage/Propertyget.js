Ext.QuickTips.init();
Ext.define('erp.controller.oa.storage.Propertyget', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
   		'oa.storage.Propertyget','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
   		'core.button.Close','core.form.YnField','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
   		'core.button.GetProperty'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpGetPropertyButton':{
    			afterrender:function(btn){
    				var isuse = Ext.getCmp("pa_isuse").value;
    				if(isuse!='0'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				var param = this.GridUtil.getAllGridStore();
					param = "[" + param.toString() + "]";
					var id = Ext.getCmp('pa_id').value;
					warnMsg('确定要确认领用资产吗?', function(btn){
						if (btn == 'yes') {
							Ext.Ajax.request({
								url:basePath + "oa/storage/Propertyget.action",
								params:{
									param:param,
									id:id
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										Ext.Msg.alert("提示", "确认资产领用成功！");
									}else{
										Ext.Msg.alert("提示", "确认资产领用失败！");
									}
								}
							});
						} else {
							return;
						}
					});
    				
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});