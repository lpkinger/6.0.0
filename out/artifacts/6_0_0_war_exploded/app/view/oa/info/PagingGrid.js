Ext.define('erp.view.oa.info.PagingGrid',{ 
	extend: 'Ext.grid.Panel', 
	layout: 'fit',
	alias: 'widget.erpPagingGridPanel',
	columnLines:true,
	initComponent : function(){ 
		var me = this,myData=me.getMemoryData();
		var pagingStore=Ext.create('Ext.data.Store', {
	        fields: [{name: 'EM_ID', type: 'number'}, 'EM_CODE', 'EM_NAME'],
	        remoteSort: true,
	        pageSize: 10,
	        data:myData,
	        proxy: {
	            type: 'pagingmemory',
	            data: myData,
	            reader: {
	                type: 'json'
	            }
	        },
	        autoLoad:true
	    });
		Ext.apply(me, { 
			columns: [{
                id:'EM_NAME',
                text: '姓名',         
                dataIndex: 'EM_NAME',
                flex: 1,
                filter: {xtype:"textfield", filterName:"EM_NAME"}
            },{
                text: 'ID',
                dataIndex: 'EM_ID',
                width: 0,
                filter: {xtype:"textfield", filterName:"EM_ID"}
            }],
			store:pagingStore,
			/*headerCt: Ext.create("Ext.grid.header.Container",{
			 	    forceFit: false,
			        sortable: true,
			        enableColumnMove:true,
			        enableColumnResize:true,
			        enableColumnHide: true
			}),*/
			plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
			selModel: Ext.create('Ext.selection.CheckboxModel',{
				checkOnly : true,
				ignoreRightMouseSelection : false
			}),
			selModel: new Ext.selection.CellModel(),
			bbar: Ext.create('Ext.PagingToolbar', {
		        pageSize: 10,
		        store: pagingStore,
		        displayInfo: true
		    })
		});
		me.callParent(arguments); 
	},
	getMemoryData:function(){
		var data=new Array();
		Ext.Ajax.request({
			url:basePath + 'oa/info/getUsersIsOnline.action', 
			async:false,
			method:'post',
			callback : function(options, success, response){
				var res = new Ext.decode(response.responseText);
				if(res.emps) data=res.emps;
			}		 
		});	
       return data;
	}
});