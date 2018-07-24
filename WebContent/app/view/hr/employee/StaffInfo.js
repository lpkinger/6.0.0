Ext.define('erp.view.hr.employee.StaffInfo',{    
	extend: 'Ext.grid.Panel', 
	alias: 'widget.staffinfo',
	id:'staffinfo',
	title:'员工资料<span style="color:gray">(所有)</span>',
	storeAutoLoad:false,
	columnLines: true,
	viewConfig: {
		stripeRows: true,
		enableTextSelection: true
	},
	initComponent : function(){
		var me=this;
		me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing',{
			clicksToEdit:1
		})];
		me.columns=[{
			dataIndex:'em_id',
			width:0,
			text:'ID'
		},Ext.create('Ext.grid.RowNumberer',{
			//text:'序号',		
			width:35
		}),{
			dataIndex:'em_code',
			width:150,
			text:'员工编号',
			align:'center',
			editor: {
				xtype: 'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false,
				allowBlank:false
			},
			renderer:columnRequired
		},{
			dataIndex:'em_name',
			width:150,
			text:'员工名称',
			align:'center',
			editor: {
				xtype: 'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false,
				allowBlank:false
			},
			renderer:columnRequired
		},{
			dataIndex:'em_defaulthscode',
			width:150,
			text:'岗位编号',
			align:'center',
			/*editor: {
				xtype: 'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false,
				allowBlank:false
			},*/
			renderer:columnRequired
		},{
			//dataIndex:'em_defaulthsname',
			dataIndex:'em_position',
			width:150,
			text:'岗位名称',
			align:'center',
			editor: {
				xtype: 'combo',
				selectOnFocus: true,
				allowOnlyWhitespace: false,
				allowBlank:false,
				field:'em_position',
				queryMode:'local',
				displayField:'jo_name',
				valueField:'jo_name',
				triggerAction:'query',
				store:Ext.create('Ext.data.Store',{
	                  fields: ['jo_name','jo_code'],
	                  proxy: {
	                     type: 'ajax',
	                     async: false,
	                      url : basePath + 'hr/employee/getJobByCondition.action',
					      extraParams: {
					        condition:"nvl(isagent,0)<>0"
					     },
	                     reader: {
	                        type: 'json',
	                        root: 'jobs'
	                     }
	                  },
	                  autoLoad:true   
	            }),
	        	listeners:{
			  		select:function(combo,records){
			  			var selected = Ext.getCmp('staffinfo').selModel.lastSelected;
			  			selected.set('em_defaulthscode', records[0].data.jo_code);
			  		}
	            }
			},
			renderer:columnRequired
		},{
			dataIndex:'em_mobile',
			width:150,
			text:'手机号码',
			align:'center',
			editor:{
				xtype:'textfield',
				regex:/^1[3|4|5|7|8][0-9]{9}$/,//  /^1[0-9]{10}$/
				regexText:'手机号必须合法',
				selectOnFocus: true,
				allowOnlyWhitespace: false,
				allowBlank:false
			},
			renderer:columnRequired
		},{
			dataIndex:'em_email',
			width:150,
			text:'邮箱',
			align:'center',
			editor:{
				xtype:'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false,
				allowBlank:false
			},
			renderer:columnRequired
		},/*{
			dataIndex:'em_defaultorname',
			width:0,
			text:'组织名称'
		},*/{
			xtype:'actioncolumn',
			width:80,
			text :'操作',
			align:'center',
			items:[{
				iconCls:'btn-delete',
				tooltip:'删除',
				width:75,
				handler:function(grid, rowIndex, colIndex) {
					Ext.Msg.confirm('删除数据?', '确定要删除当前选中行(行号:'+(rowIndex+1)+')?',
							function(choice) {
						if(choice === 'yes') {
							var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;
							if(record.data.em_id=="0" || record.data.em_id=="" ||record.data.em_id==null){
								grid.store.remove(record);
								return;
							}
							gridpanel.removeDetail(gridpanel,record);
						}
					});   
				}
			}]
		},{
			dataIndex:'em_defaultorid',
			width:0,
			text:'组织id'
		}];
		me.store=Ext.create('Ext.data.Store',{
			fields:[{name:'em_id',type:'int'},
			        {name:'em_code',type:'string'},
			        {name:'em_name',type:'string'},
			        {name:'em_defaulthscode',type:'string'},
//			        {name:'em_defaulthsname',type:'string'},
			        {name:'em_defaultorid',type:'int'},
			        {name:'em_position',type:'string'},
//			        {name:'em_defaultorname',type:'string'},////
			        {name:'em_mobile',type:'string'},
			        {name:'em_email',type:'string'}],
			        proxy: {
			        	type: 'ajax',
			        	url: basePath+'/hr/employee/getEmployees.action',
			        	api: {
							create:  basePath+'hr/employee/saveEmployeess.action?enUU='+enUU,
							update:  basePath+'hr/employee/updateEmployeess.action'
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
			        		param:{condition:'StaffInfo'},
			        		root: 'employees'
			        	}
			        }, 
			        autoLoad:me.storeAutoLoad  
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
	deleteEmployee:function(){
		var employeegrid=Ext.getCmp('staffinfo');
		selectionModel=employeegrid.getSelectionModel(),
		selecteds = selectionModel.getSelection(),datas=new Array();
		Ext.Array.each(selecteds,function(item){
			if(item.get('em_id')) datas.push(Ext.JSON.encode(item.data));
		});
		Ext.Ajax.request({
			url : basePath + 'hr/employee/deleteEmployees.action',
			params: {
				jsonData:unescape(datas.toString()) 
			},
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				showResult('提示','删除成功!');
				employeegrid.getStore().load();
                 
			}
		});

	},
	removeDetail:function(grid,record){
		/*var staffinfo=Ext.getCmp('staffinfo');
		var record = staffinfo.getStore();
		var items = record.data.items;*/
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath +'hr/emplmana/deleteEmployee.action',
			params: {
				id:record.get('em_id'),
				caller:'EmployeeManager'	
			},
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					//showError(localJson.exceptionInfo);
					showResult('提示','没有保存数据，无法删除');
					return;
				}
				if(localJson.success){
					showResult('提示','删除成功!');
					grid.getStore().remove(record);
					//grid.getStore().load();
				}
			}
		});
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