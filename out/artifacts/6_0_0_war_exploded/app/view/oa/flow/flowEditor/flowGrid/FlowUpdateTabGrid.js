Ext.define('erp.view.oa.flow.flowEditor.flowGrid.FlowUpdateTabGrid',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.FlowUpdateTabGrid',
	layout:{
	    type: 'hbox',
	    align: 'stretch',
	    padding: 5
    },
    id:'FlowUpdateTabGrid',
    height:window.innerHeight - 172,
	columnWidth: 1,
	bodyStyle:'background:#f2f2f2;border-top:none;padding-top:5px;',
	autoScroll : true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){
		var me = this;
		console.log(me._groupName);
		console.log(caller);
		console.log(shortName);
		var allFields = new Array();
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
					allFields.push({
						text:item.FD_CAPTION,
						field:item.FD_FIELD
					});
				});
			}
		});	
		//取得当前字段和所有字段
		var nowFields = me.getNowFields(shortName,me._groupName);
		var copyAllField = allFields.concat();//复制所有字段信息
		if(!nowFields){
			nowFields = new Array();
		}
		if(!allFields){
			allFields = new Array();
		}
		//剔除重复field
		if(nowFields.length>0&&allFields.length>0){
			Ext.Array.each(nowFields, function(b_item){
				Ext.Array.each(allFields, function(a_item,index){
					if(a_item&&a_item.field==b_item.field){
						allFields.splice(index,1)
						return;
					}
				});
			});
		}
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
			   data: allFields,
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
			            data:nowFields,
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
	},
	//读取已加载字段
	getNowFields:function(shortName,name){
		var nowFields = new Array();
		Ext.Ajax.request({
			url : basePath + 'oa/flow/getSelectTab.action',
			async:false,
			params:{
				shortName:shortName,
				groupName:name,
				caller:caller
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.groups.length>0){
					Ext.Array.each(rs.groups, function(item){
						var isNew = item.FGC_NEW?item.FGC_NEW:false
						if(isNew=='true'){isNew = true}else if(isNew=='false'){isNew = false}
						var main = item.FGC_REQUIREDFIELD?item.FGC_REQUIREDFIELD:false
						if(main=='true'){main = true}else if(main=='false'){main = false}
						var read = item.FGC_READ?item.FGC_READ:false
						if(read=='true'){read = true}else if(read=='false'){read = false}
						nowFields.push({
							fgc_role : item.FGC_ROLE,
							fgc_rolecode : item.FGC_ROLECODE,
							fgc_id : item.FGC_ID,
							logic: item.FD_LOGICTYPE,
							text : item.FD_CAPTION,
							field : item.FGC_FIELD,
							main : main,
							isNew : isNew,
							read : read,
							columnsWidth : item.FGC_WIDTH
						});
					});
				}
			}
		});
		return nowFields;
	}
});