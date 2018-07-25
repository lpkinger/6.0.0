Ext.require('Ext.ux.CellDragDrop');
Ext.define('erp.view.sys.hr.StandardJobGrid',{    
	extend: 'Ext.grid.Panel', 
	alias: 'widget.standardjobgrid',
	title:'标准岗位资料',
	columnLines: true,
	viewConfig: {
		stripeRows: true,
		plugins: {
			ptype: 'celldragdrop',
			applyEmptyText:false,
			dropBackgroundColor: Ext.themeName === 'neptune' ? '#a4ce6c' : 'green',
			noDropBackgroundColor: Ext.themeName === 'neptune' ? '#d86f5d' : 'red',
			enforceType: true,
			enableDrop :false
		}
	},
    listeners :{
    	itemmousedown:function(view,record,item){
    		var oCt=view.ownerCt.ownerCt,detailPanel=oCt.down('panel[itemId=detailPanel]');
    		detailPanel.update({name:record.get('jo_orgName')+'→'+record.get('jo_name'),description:record.get('jo_description')});
    	}
	},
	initComponent : function(){
		var me=this;
		me.columns=[{
			dataIndex:'jo_id',
			width:0,
			text:'ID'
		},Ext.create('Ext.grid.RowNumberer',{
			width:35
		}),{
			dataIndex:'jo_code',
			width:100,
			text:'岗位编号'
		},{
			dataIndex:'jo_name',
			width:120,
			text:'岗位名称',
			enableDrop:false,
			enableDrag:false
		},{
			dataIndex:'jo_orgName',
			width:120,
			text:'所属组织名称'
		},{
			dataIndex:'jo_orgId',
			width:0,
			text:'组织ID'
		}];
		me.store=Ext.create('Ext.data.Store',{
			fields:[{name:'jo_id',type:'number'},
			        {name:'jo_code',type:'string'},
			        {name:'jo_name',type:'string'},
			        {name:'jo_orgcode',type:'string'},
			        {name:'jo_orgName',type:'string'},
			        {name:'jo_orgId',type:'int'},
			        {name:'jo_description',type:'string'}],
			        proxy: {
			        	type: 'ajax',
			        	url: basePath+'/hr/employee/getJobs.action',
			        	extraParams :{
			        		isStandard:1
			        	},
			        	reader: {
			        		type: 'json',
			        		root: 'jobs'
			        	}
			        }, 
			        autoLoad:true  
		});
		this.callParent(arguments);
	},
	loadNewStore: function(grid, param){
		var me = this;
		param=param||grid.params;
		grid.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + "common/loadNewGridStore.action",
			params: param,
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				var data = res.data;
				if(!data || data.length == 0){
					grid.store.removeAll();
					me.add10EmptyItems(grid);
				} else {
					grid.store.loadData(data);
				}
				//自定义event
				grid.addEvents({
					storeloaded: true
				});
				grid.fireEvent('storeloaded', grid, data);
			}
		});
	},
	removeDetail:function(grid,record){
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'hr/employee/deleteJob.action',
			params: {
				id: record.get('jo_id')
			},
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.success){
					showResult('提示','删除成功!');
					grid.getStore().remove(record);
					/*	var orgTree = Ext.getCmp('orgtree'),
					selectionModel=orgTree.getSelectionModel(),
		            selectedList = selectionModel.getSelection()[0];
					grid.getStore().load({params:{
    		    		orgid:selectedList.get('or_id')
    		    	}});*/
				}
			}
		});
	},
	setColumns:function(columns){
		Ext.Array.each(columns,function(column){
			if(column.xtype=='yncolumn'){
				column.xtype='checkcolumn';
				column.editor= {
						xtype: 'checkbox',
						cls: 'x-grid-checkheader-editor'
				};
			}
		});
		return columns;
	},
	DetailUpdateSuccess:function(btn,type){
		var tabP=Ext.getCmp('saletabpanel'),_activeTab=tabP.activeTab;
		_activeTab.loadNewStore(_activeTab,_activeTab.params);
		var win=btn.up('window');
		if(win) win.close();
	},
	isVal:function(){
		var records=this.getStore().getModifiedRecords(),cm=this.columns,necessaryCM=new Array(),flag=true;
		Ext.Array.each(cm,function(c){
			if(c.editor && !c.editor.allowBlank){
				necessaryCM.push(c.dataIndex);
			}
		});
		Ext.Array.each(records,function(r){
			var o=r.data;
			for( n in o){
				if(Ext.Array.contains(necessaryCM,n) && !o[n]){
					flag=false;
					return flag;
				}
			}
		});
		return flag;
	}
})