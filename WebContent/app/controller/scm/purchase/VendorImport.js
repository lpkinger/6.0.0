Ext.define('erp.controller.scm.purchase.VendorImport', {
    extend: 'Ext.app.Controller',
  	FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:['scm.purchase.vendorImportFromB2B.VendorImport',
    'scm.purchase.vendorImportFromB2B.VendorImportForm',
    'scm.purchase.vendorImportFromB2B.VendorImportGrid', 
    'core.trigger.Dropdown',
    'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger',
    'core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField', 'core.form.FileField'
   ],
    init:function(){
    	var me = this;
		me.control({
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
