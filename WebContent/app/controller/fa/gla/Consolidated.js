Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.Consolidated', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gla.Consolidated','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpFormPanel':{
    			afterload:function(form){
    				if(mastercode){
    					parent.Ext.getCmp(mastercode).on('activate',function(panel, reload){
    						me.setData(form, reload);
    					});
	    			}
    				me.setData(form, false);
    				form.hide();
    			}
    		},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	setData:function(form, reload){
		var me = this;
		var yearmonth1 = parent.Ext.getCmp('yearmonth').value,
		fatype1 = parent.Ext.getCmp('fatype').value;
		
		if(yearmonth1!=yearmonth||fatype1!=fatype||reload){
			var change = fatype1!=fatype;
			reload ? me.hideColumn(fatype1, change) : setTimeout(function(){
				me.hideColumn(fatype1, change)
			}, 20);
			
			me.FormUtil.setLoading(true);
			var fields = '';
			Ext.Array.each(form.items.items,function(field){
				fields += ',' + field.name;
			});
			fields = fields.substring(1);
			Ext.Ajax.request({
		   		url : basePath + 'fa/gla/getChildReport.action',
		   		params: {
		   			fields: fields,
		   			yearmonth: yearmonth1,
		   			mastercode: mastercode,
		   			fatype: fatype1,
		   			kind: '集团报表'
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			me.FormUtil.setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   			}
	    			if(localJson.success){
	    				form.getForm().setValues(localJson.data);
    					var grid = Ext.getCmp('grid');
    					var param = {caller: grid.caller||caller,condition:'crd_crid='+localJson.data[form.keyField]};
    					me.GridUtil.loadNewStore(grid, param);
    					yearmonth = yearmonth1;
    					fatype = fatype1;
		   			}
		   		}
			});
		}
	},
	hideColumn: function(fatype, change){
		if(!change){
			return;
		}
		var grid = Ext.getCmp('grid');
		
		if(fatype != '资产负债表'){
			grid.down('gridcolumn[dataIndex=crd_rightitem]').hide();
			grid.down('gridcolumn[dataIndex=crd_rightstep]').hide();
			grid.down('gridcolumn[dataIndex=crd_rightbbamount1]').hide();
			grid.down('gridcolumn[dataIndex=crd_rightbbamount2]').hide();
			grid.down('gridcolumn[dataIndex=crd_bbamount1]').setText('本月数');
			grid.down('gridcolumn[dataIndex=crd_bbamount2]').setText('本年累计数');
		} else {
			grid.down('gridcolumn[dataIndex=crd_rightitem]').show();
			grid.down('gridcolumn[dataIndex=crd_rightstep]').show();
			grid.down('gridcolumn[dataIndex=crd_rightbbamount1]').show();
			grid.down('gridcolumn[dataIndex=crd_rightbbamount2]').show();
			grid.down('gridcolumn[dataIndex=crd_bbamount1]').setText('年初数');
			grid.down('gridcolumn[dataIndex=crd_bbamount2]').setText('年末数');
		}
	}
});