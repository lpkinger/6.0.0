Ext.QuickTips.init();
Ext.define('erp.controller.oa.storage.ReturnProperty', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
   		'oa.storage.ReturnProperty','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
   		'core.button.Close','core.form.YnField','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
   		'core.button.ReturnProperty'
   	],
    init:function(){
    	//var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpReturnPropertyButton':{
    			afterrender:function(btn){
    				var isuse = Ext.getCmp("pa_isover").value;
    				if(isuse!='0'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				var param = this.GridUtil.getAllGridStore();
					param = "[" + param.toString() + "]";
					var id = Ext.getCmp('pa_id').value;
					warnMsg('确定要确认归还资产吗?', function(btn){
						if (btn == 'yes') {
							Ext.Ajax.request({
								url:basePath + "oa/storage/ReturnProperty.action",
								params:{
									param:param,
									id:id
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										Ext.Msg.alert("提示", "确认资产归还成功！");
									}else{
										Ext.Msg.alert("提示", "确认资产归还失败！");
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