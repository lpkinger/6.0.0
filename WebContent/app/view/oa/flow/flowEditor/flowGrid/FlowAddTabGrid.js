Ext.define('erp.view.oa.flow.flowEditor.flowGrid.FlowAddTabGrid',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.FlowAddTabGrid',
	layout:{
	    type: 'hbox',
	    align: 'stretch',
	    padding: 5
    },
    id:'FlowAddTabGrid',
    height:window.innerHeight - 172,
	columnWidth: 1,
	bodyStyle:'background:#f2f2f2;border-top:none;padding-top:5px;',
	autoScroll : true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){
		var allField = new Array();
		//获取所有字段
		var s = '(select fo_id from form where fo_caller = \''+caller + '\') order by fd_detno ';
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fd_caption,fd_field',
				caller : 'formdetail',
				condition : 'fd_foid = ' + s
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				Ext.Array.each(Ext.decode(rs.data), function(item){
					allField.push({
						text:item.FD_CAPTION,
						field:item.FD_FIELD
					});
				});
			}
		});
		var me = this;	
		me.allField = allField;
	    var items = [{
	       margin:'0 10 0 10',
		   xtype:'grid',
		   width:200,
		   multiSelect: true,
		   id: 'fromTab',
		   title:'所有字段',
		   cls: 'custom-grid',	
		   store:Ext.create('Ext.data.Store', {
		   fields: [{name:'text',type:'string'},{name:'field',type:'string'},{name:'isNew',type:'bool'},
		            {name:'columnsWidth',type:'string'}],
			   data: allField,
			   filterOnLoad: false 
		   }),
		   plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
		   viewConfig: {
			   plugins: {
				   ptype: 'gridviewdragdrop',
			   	   dragGroup: 'toTab',
			       dropGroup: 'toTab'
			   }	    					
		   },
		   listeners: {
		   	   itemdblclick : function(view, record){ 
		   	   		var addData = record.data;
		   	   		Ext.getCmp('toTab').store.add(addData);
		   	   		Ext.getCmp('fromTab').store.remove(record);
		   	   }
		   },
		   stripeRows: false,
		   columnLines:true,
		   columns:[{
			   dataIndex:'text',
			   cls :"x-grid-header-1",
			   text:'字段名称',
			   flex:1,
			   filter: {
			   	xtype : 'textfield'
			   },
			   renderer : function(value, metaData, record, rowIndex, colIndex) {
	                metaData.tdAttr = 'qclass="x-tip" data-qtitle="名称--字段类型：" data-qwidth="auto" data-qtip="'
	                        + value +'--'+ record.get('field') + '"';
	                return value;
	           }
		   }]
	   },{
		   xtype:'grid',
		   margin:'0 10 0 10',
		   multiSelect: true,
		   width:450,
		   id: 'toTab',
		   stripeRows: true,
		   columnLines:true,
		   title:'使用字段',
		   store:Ext.create('Ext.data.Store', {
		   fields: [{name:'text',type:'string'},{name:'field',type:'string'},{name:'isNew',type:'bool'},
		   			{name:'columnsWidth',type:'string'}],
			            data:[]/*me.nowData*/,
			            filterOnLoad: false 
		   }),
		   necessaryField:'fullName',
		   plugins: [Ext.create('erp.view.core.grid.HeaderFilter'),
	         		 Ext.create('Ext.grid.plugin.CellEditing', {
	        	 		clicksToEdit: 1
	       })],
           viewConfig: {
        	 plugins: {
        		 ptype: 'gridviewdragdrop',
        		 dragGroup: 'toTab',
        		 dropGroup: 'toTab'
        	 }
           },
           listeners: {
		   	   itemdblclick : function(view, record){ 
		   	   		var addData = record.data;
		   	   		Ext.getCmp('fromTab').store.add(addData);
		   	   		Ext.getCmp('toTab').store.remove(record);
		   	   }
		   },
           columns:[{
        	 dataIndex:'text',
        	 text:'字段名称',
        	 cls :"x-grid-header-1",
        	 width:200,
        	 filter: {
        		 xtype : 'textfield'
        	 },
        	 renderer : function(value, metaData, record, rowIndex, colIndex) {
	                metaData.tdAttr = 'qclass="x-tip" data-qtitle="名称--字段类型：" data-qwidth="auto" data-qtip="'
	                        + value +'--'+ record.get('field') + '"';
	                return value;
	         }
           },{
        	 dataIndex:'isNew',
        	 xtype:"checkcolumn",
        	 text:'全新',
        	 cls :"x-grid-header-1",
        	 flex:1,
        	 autoEdit:false,
			 modify:false,
			 useNull:false,
			 width:80,
        	 editor: {
			   	cls:"x-grid-checkheader-editor",
				displayField:"display",
				editable:true,
				format:"",
				hideTrigger:false,
				maxLength:4000,
				minValue:null,
				positiveNum:false,
				queryMode:"local",
				store:null,
				valueField:"value",
				xtype:"checkbox"
		   	 }
           },{
        	 dataIndex:'columnsWidth',
        	 text:'列宽度（1~4）',
        	 xtype:'numbercolumn',
        	 format: '0',
        	 cls :"x-grid-header-1",
        	 flex:1,
        	 editor: {
			   	xtype: 'numberfield',
			   	allowBlur : true,
			   	displayField : "display",
				valueField : "value",
				hideTrigger:true,
				positiveNum:false,
				queryMode:"local"
		   	 }
           }] 
	    }]
	    Ext.apply(me, { 
			items:items 
		}); 
	    this.callParent(arguments);				   
	}
});