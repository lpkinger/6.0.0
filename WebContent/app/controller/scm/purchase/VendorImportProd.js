Ext.define('erp.controller.scm.purchase.VendorImportProd', {
    extend: 'Ext.app.Controller',
  	FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:['scm.purchase.vendorImportFromB2B.VendorImportProd',
   'scm.purchase.vendorImportFromB2B.VendorImportProdGrid',
    'scm.purchase.vendorImportFromB2B.VendorImportProdFrom',
    'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger',
    'core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField', 'core.form.FileField'
   ],
    init:function(){
    	var me = this;
		me.control({
			'erpVendorImportProdFormPanel':{
    			afterrender :function(){
	    			var form = Ext.getCmp('erpVendorImportProdFormPanel');
    				var tabid = getUrlParam("tabid");
    				var condition = form.getCondition();
    				//form.searchInit = true 未初始化过前一个界面物料条件
    				if(tabid &&condition==" 1=1 "&&form.searchInit){
    					var tab = parent.Ext.getCmp(tabid);
        				var datas = tab.searchcondition;
    					var formData = form.getValues();
    						formData['pr_title'] = datas['pr_title'];
        					formData['pr_spec'] = datas['pr_spec'];
        					formData['pr_brand'] = datas['pr_brand'];
        					formData['pr_cmpcode'] = datas['pr_cmpcode'];
        					formData['pr_kind'] = datas['pr_kind'];
							form.getForm().setValues(formData);
							form.getForm().getFields().each(function (item,index,length){
								item.originalValue = item.value;
							});
        			}
    			}
    		},
			'textfield':{
				specialkey : function(field, e){
	        		if(e.getKey() == Ext.EventObject.ENTER&&field&&(!field.itemId)){
	        			var query = Ext.getCmp('query');
	        			query.fireEvent('click',query);
	        		}
	        	}
    		}
    	});
    }
});
