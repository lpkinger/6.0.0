Ext.QuickTips.init();
Ext.define('erp.controller.data.Tidy', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'data.GridPanel','data.Tidy','data.GridPanel2','data.GridPanel3','data.GridPanel4',
    		'data.Grid6','data.Grid7','data.Grid8','data.Grid9'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
//    		'erpGridPanel':{
//    			itemclick: function(selModel, record){
//    				
//    			}
//    		},
//    		'button[id=tidy]': {
//    			click: function(btn){
//    	    		me.query();
//    			}
//    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	query: function(){
		var me = this;
		var url = "common/dataTidy.action";
		me.FormUtil.getActiveTab().setLoading(true);
		Ext.Ajax.request({//拿到form的items
        	url : basePath + url,
        	params: {
        		em_uu:em_uu
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.FormUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		console.log(res);
        		if(res.success){
        			if(res.list1.length>0){//数据字典中缺省的表
        				var grid = Ext.getCmp('grid1');
        				Ext.each(res.list1,function(dc, index){
        					grid.getStore().insert(index, {'table_name': dc.table_name});
        				});
        				grid.setTitle(grid.title + ' (' + res.list1.length + ')');
        			}
        			if(res.list2.length>0){//数据字典中有而数据库中没有的表
        				var grid = Ext.getCmp('grid2');
        				Ext.each(res.list2,function(dc, index){
        					grid.getStore().insert(index, {'dd_tablename': dc.dd_tablename});
        				});
        				grid.setTitle(grid.title + ' (' + res.list2.length + ')');
        			}
//        			if(res.list3.length>0){//数据字典详细表中有而表结构缺省的字段
//        				var grid = Ext.getCmp('grid3');
//        				Ext.each(res.list3,function(dc, index){
//        					grid.getStore().insert(index, {'ddd_tablename': dc.ddd_tablename, 'ddd_fieldname': dc.ddd_fieldname, 'ddd_fieldtype': dc.ddd_fieldtype});
//        				});
//        				grid.setTitle(grid.title + ' (' + res.list3.length + ')');
//        			}
//        			if(res.list4.length>0){//数据字典详细表中缺省的字段
//        				var grid = Ext.getCmp('grid4');
//        				Ext.each(res.list4,function(dc, index){
//        					grid.getStore().insert(index, {'table_name': dc.table_name,'column_name': dc.column_name,'data_type': dc.data_type,'data_length': dc.data_length});
//        				});
//        				grid.setTitle(grid.title + ' (' + res.list4.length + ')');
//        			}
        		}
        	}
		});
	}
});