Ext.define('erp.view.oa.flow.flowEditor.flowGrid.FlowCommitGrid',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.FlowCommitGrid',
	layout:{
	    type: 'hbox',
	    align: 'stretch',
	    padding: 5
    },
    id:'FlowCommitGrid',
	bodyStyle:'background:#f2f2f2;border-top:none;padding-top:5px;',
	autoScroll : true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){
		var me = this;	
	    var items = [{
	       margin:'0 10 0 10',
		   xtype:'grid',
		   width:250,
		   multiSelect: true,
		   id: 'fromgrid',
		   title:'所有字段',
		   cls: 'custom-grid',	
		   store:Ext.create('Ext.data.Store', {
		   fields: [{name:'text',type:'string'},{name:'field',type:'string'},{name:'isNew',type:'bool'},
		   			{name:'read',type:'bool'},{name:'main',type:'bool'},{name:'columnsWidth',type:'string'},
		   			{name:'fgc_id',type:'string'},{name:'fgc_role',type:'string'},{name:'fgc_rolecode',type:'string'},
		   			{name:'logic',type:'string'}],
			   data: me.allFields,
			   filterOnLoad: false 
		   }),
		   plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
		   viewConfig: {
			   plugins: {
				   ptype: 'gridviewdragdrop',
			   	   dragGroup: 'togrid',
			       dropGroup: 'togrid'
			   }	    					
		   },
		   listeners: {
		   	   itemdblclick : function(view, record){ 
		   	   		var addData = record.data;
		   	   		Ext.getCmp('togrid').store.add(addData);
		   	   		Ext.getCmp('fromgrid').store.remove(record);
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
		   flex:1,
		   id: 'togrid',
		   stripeRows: true,
		   columnLines:true,
		   title:'使用字段',
		   store:Ext.create('Ext.data.Store', {
		   fields: [{name:'text',type:'string'},{name:'field',type:'string'},{name:'isNew',type:'bool'},
		   			{name:'read',type:'bool'},{name:'main',type:'bool'},{name:'columnsWidth',type:'string'},
		   			{name:'fgc_id',type:'string'},{name:'fgc_role',type:'string'},{name:'fgc_rolecode',type:'string'},
		   			{name:'logic',type:'string'}],
			            data:me.nowFields,
			            filterOnLoad: false 
		   }),
		   necessaryField:'fullName',
		   plugins: [Ext.create('erp.view.core.grid.HeaderFilter'),
	         		 Ext.create('Ext.grid.plugin.CellEditing', {
	        	 		clicksToEdit: 1
	       })],
	       listeners: {
		   	   itemdblclick : function(view, record){ 
		   	   		var addData = record.data;
		   	   		Ext.getCmp('fromgrid').store.add(addData);
		   	   		Ext.getCmp('togrid').store.remove(record);
		   	   }
		   },
           viewConfig: {
        	 plugins: {
        		 ptype: 'gridviewdragdrop',
        		 dragGroup: 'togrid',
        		 dropGroup: 'togrid'
        	 }
           },
           columns:[{
        	 dataIndex:'text',
        	 text:'字段名称',
        	 cls :"x-grid-header-1",
        	 flex:1.5,
        	 filter: {
        		 xtype : 'textfield'
        	 },
        	 renderer : function(value, metaData, record, rowIndex, colIndex) {
	                metaData.tdAttr = 'qclass="x-tip" data-qtitle="名称--字段类型：" data-qwidth="auto" data-qtip="'
	                        + value +'--'+ record.get('field') + '"';
	                return value;
	           }
           },{
        	 dataIndex:'main',
        	 xtype:"checkcolumn",
        	 text:'必填',
        	 cls :"x-grid-header-1",
        	 width:80,
        	 autoEdit:false,
			 modify:false,
			 useNull:false,
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
        	 dataIndex:'read',
        	 xtype:"checkcolumn",
        	 text:'只读',
        	 cls :"x-grid-header-1",
        	 width:80,
        	 autoEdit:false,
			 modify:false,
			 useNull:false,
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
        	 dataIndex:'isNew',
        	 xtype:"checkcolumn",
        	 text:'全新',
        	 cls :"x-grid-header-1",
        	 width:80,
        	 autoEdit:false,
			 modify:false,
			 useNull:false,
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
        	 width:120,
        	 editor: {
			   	xtype: 'numberfield',
			   	allowBlur : true,
			   	displayField : "display",
				valueField : "value",
				hideTrigger:true,
				positiveNum:false,
				queryMode:"local"
		   	 }
           },{
        	 dataIndex:'fgc_role',
        	 text:'操作角色',
        	 format: '0',
        	 cls :"x-grid-header-1",
        	 width:120,
        	 editor: {
        	 	editable: false,
			   	xtype: 'combo',
			   	allowBlur : true,
			   	displayField: 'display',
    			valueField: 'display',
				positiveNum:false,
				queryMode:"local",
				store: Ext.create('Ext.data.Store',{
					fields: ['display','value'],
					data:[{value:"duty", display:"责任人"},{value:"actor", display:"参与人"},
	    				  {value:"reader", display:"读者"},{value:"", display:"空"}]
			 	}),
			 	listeners:{
			   	 	change:function(f,newValue,oldValue){
			   	 		if(newValue){
			   	 			var to = Ext.getCmp('togrid');
			   	 			//赋值
			   	 			if(newValue=='责任人'){
			   	 				to.selModel.lastSelected.data.fgc_rolecode = 'duty';
			   	 			}else if(newValue=='参与人'){
			   	 				to.selModel.lastSelected.data.fgc_rolecode = 'actor';
			   	 			}else if(newValue=='读者'){
			   	 				to.selModel.lastSelected.data.fgc_rolecode = 'reader';
			   	 			}else if(newValue=='创建人'){
			   	 				to.selModel.lastSelected.data.fgc_rolecode = 'creator';
			   	 			}else{
			   	 				to.selModel.lastSelected.data.fgc_rolecode = '';
			   	 				f.setValue('');
			   	 			}
			   	 		}
			   	 	}
			   	 }
		   	 }
		   	 
           }] 
	    }]
	    Ext.apply(me, { 
			items:items 
		}); 
	    this.callParent(arguments);				   
	}
});