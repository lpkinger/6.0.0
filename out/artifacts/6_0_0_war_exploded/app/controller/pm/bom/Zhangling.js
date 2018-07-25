Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.Zhangling', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.bom.Zhangling','core.form.Panel', 'core.trigger.DbfindTrigger',
    		'core.button.Print','core.button.Zhangling','core.button.Close'
    	],
    init:function(){
    	this.control({ 
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpZhanglingButton': {
    			click: function(btn) {
    				var form = btn.ownerCt.ownerCt,
    					todate = Ext.getCmp('ba_date').value;
    					form.setLoading(true);
        				Ext.Ajax.request({
        					url: basePath + 'pm/bom/Zhangling.action',
        					params: {
        						todate: todate
        					},
        					timeout: 120000,
        					callback: function(opt, s, r) {
        						form.setLoading(false);
        						var rs = Ext.decode(r.responseText);
        						if(rs.success) {
        							alert('计算完成!');
        						}
        					}
        				});
    				//}
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
					var reportName = "BOMCostView",
						id = Ext.getCmp('bo_id').value, 
						condition = '{BOM.bo_id}=' + id;
					console.log(condition);
					this.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		}
    		
    	});
    }
});