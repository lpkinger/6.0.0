Ext.define('erp.view.sys.hr.EmployeeGrid',{    
	extend: 'Ext.grid.Panel', 
	//store:Ext.data.StoreMgr.lookup('employeeStore'),
	alias: 'widget.employeegrid',
	id:'employeegrid',
	selModel: new Ext.selection.CheckboxModel(),
	//title:'人事资料<span style="color:gray">(所有)</span>',
	columnLines: true,
	viewConfig: {
		stripeRows: true,
		enableTextSelection: true
	},
	forceFit:true,
	dockedItems: [{
		xtype: 'toolbar',
		ui: 'footer',
		items: [{
			xtype:'tbtext',
			text:'<span style="font-weight:bold;" >人事资料</span>'
		},{ xtype: 'tbseparator' },{
			text:'添加',
			itemId: 'addemployee',
			tooltip:'添加新记录',
			iconCls:'btn-add'
		},'-',{
			text:'保存',
			itemId:'saveemployee',
			tooltip:'保存',
			iconCls:'btn-save'
		},'-',{
			text:'删除',
			itemId:'deleteemployee',
			tooltip:'删除',
			iconCls:'btn-delete'
		},'-',{
			text:'帮助',
			iconCls:'btn-help',
			tooltip:'帮助简介'
		},{
			xtype:'tbtext',
			text:'<div style="color:gray;">带'+required+'为必填项</div>'
		}]
	}],
	columns:[{
		dataIndex:'em_id',
		width:0,
		text:'ID'
	},{
		dataIndex:'em_code',
		width:100,
		text:'员工编号',
		renderer:columnRequired,
		editor: {
			xtype: 'textfield',
			selectOnFocus: true,
			allowOnlyWhitespace: false,
			allowBlank:false
		}
	},{
		dataIndex:'em_name',
		width:120,
		text:'员工名称',
		renderer:columnRequired,
		editor: {
			xtype: 'textfield',
			selectOnFocus: true,
			allowOnlyWhitespace: false,
			allowBlank:false
		}
	},{
		dataIndex:'em_sex',
		width:50,
		text:'性别',
		editor: {
			xtype: 'combo',
			selectOnFocus: true,
			allowOnlyWhitespace: false,
			allowBlank:false,
			store: Ext.create('Ext.data.Store', {
				fields: ['display', 'value'],
				data : [
				        {"display":"男", "value":"男"},
				        {"display":"女", "value":"女"}		          
				        ]
			}),
			queryMode: 'local',
			displayField: 'display',
			valueField: 'value',
			listConfig :{
				minWidth:40
			}
		}
	},{
		dataIndex:'em_mobile',
		width:120,
		text:'手机号',
		renderer:columnRequired,
		editor:{
			xtype:'numberfield',
			regex:new RegExp('^[1][358][0-9]{9}'),
			regexText:'请填写11位有效手机号码' 
		}
	},{
		dataIndex:'em_email',
		width:200,
		text:'邮箱',
		renderer:columnRequired,
		editor: {
			allowBlank: false,
			vtype: 'email'
		}
	},{
		dataIndex:'em_position',
		width:120,
		text:'岗位',
		renderer:columnRequired,
		editor:{
			allowBlank:false,
			xtype:'dbfindtrigger',
			dbfind:'Job|jo_name'
		}
	},{
		dataIndex:'em_defaulthsid',
		width:0,
		text:'岗位ID'
	},{
		dataIndex:'em_defaulthsname',
		width:0,
		text:'岗位名称'
	},{
		dataIndex:'em_defaultorname',
		width:150,
		text:'所属组织'
	},{
		dataIndex:'em_defaultorid',
		width:0,
		text:'所属组织ID'
	},{
		dataIndex:'em_depart',
		width:120,
		text:'财务核算部门',
		flex:1
	}/*,{
		xtype:'actioncolumn',
		width:45,
		text :'操作',
		items:[{
			iconCls:'btn-delete',
			tooltip:'删除',
			width:75,
			handler:function(grid, rowIndex, colIndex) {
				Ext.Msg.confirm('删除数据?', '确定要删除当前选中行(行号:'+(rowIndex+1)+')?',
						function(choice) {
					if(choice === 'yes') {
						var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;
						gridpanel.removeDetail(gridpanel,record);
					}
				});   
			}
		}]
	}*/],
	dbfinds:[{dbGridField:'jo_id',field:'em_defaulthsid'},
	         {dbGridField:'jo_name',field:'em_position'},
	         {dbGridField:'jo_name',field:'em_defaulthsname'},   
	         {dbGridField:'jo_orgid',field:'em_defaultorid'},
	         {dbGridField:'jo_orgname',field:'em_defaultorname'},
	         {dbGridField:'or_department',field:'em_depart'}],
	         store:Ext.create('Ext.data.Store',{
	        	 fields:[{name:'em_id',type:'number'},
	        	         {name:'em_code',type:'string'},
	        	         {name:'em_name',type:'string'},
	        	         {name:'em_sex',type:'string'},
	        	         {name:'em_mobile',type:'string'},
	        	         {name:'em_email',type:'string'},
	        	         {name:'em_position',type:'string'},
	        	         {name:'em_defaulthsid',type:'int'},
	        	         {name:'em_defaulthsname',type:'string'},
	        	         {name:'em_defaultorname',type:'string'},
	        	         {name:'em_defaultorid',type:'int'},
	        	         {name:'em_depart',type:'string'}],
	        	         proxy: {
	        	        	 type: 'ajax',
	        	        	 url: basePath+'/hr/employee/getEmployees.action',
	        	        	 api: {
	        	        		 create:  basePath+'hr/employee/saveEmployees.action',
	        	        		 update:  basePath+'hr/employee/updateEmployees.action',
	        	        	 },
	        	        	 writer : {
	        	        		 type : 'json',
	        	        		 root : 'jsonData',
	        	        		 encode : true,
	        	        		 nameProperty:'data',
	        	        		 allowSingle : false
	        	        	 },
	        	        	 reader: {
	        	        		 type: 'json',
	        	        		 root: 'employees'
	        	        	 }
	        	         }, 
	        	         autoLoad: true       
	         }),
	         initComponent : function(){
	        	 var me=this;
	        	 me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing')];
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
	        				 var orgTree = Ext.getCmp('orgtree'),
	        				 selectionModel=orgTree.getSelectionModel(),
	        				 selectedList = selectionModel.getSelection()[0];
	        				 grid.getStore().load({params:{
	        					 orgid:selectedList.get('or_id')
	        				 }});
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
	         }
})