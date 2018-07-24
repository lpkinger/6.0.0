Ext.define('erp.view.sys.hr.OrGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.orGridpanel',
	layout : 'fit',
	id: 'orgridpanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    title:'组织资料',
    plugins: [
    Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	})],
    dockedItems : [{
    	xtype: 'toolbar',
    	ui: 'footer',
    	dock: 'top',
    	hidden:true,
    	items: [{
    		name: '',
    		text: '新增人员',
    		iconCls:'btn-add',
        	cls: 'x-btn-gray',
        	id:'empAdd',
        	handler:function(){
        		var me = this,
        		orgTree = Ext.getCmp('ortreepanel'),
				selectionModel = orgTree.getSelectionModel(),
				selectedList = selectionModel.getSelection()[0];
				employeegrid=Ext.getCmp('orgridpanel'); 
				employeegrid.store.insert(0, {'em_id':'0','em_name':'','em_sex':'男','em_defaultorid':selectedList.data.id,'em_defaultorname':selectedList.data.or_name});
				edit.startEditByPosition({
					row: 0,
					column: 1
				});
		   }
    	},{
    		id:'export',
			name: 'export',
			text:'批量导入',
			iconCls: 'x-button-icon-excel',
	    	margin: '0 4 0 0',
	    	handler:function(){
		    	var url='jsps/common/import.jsp?whoami=Employee&title=员工资料';
		    	window.open(basePath+url,'_blank');
	    	}
		}]
    }],
  	store:[],
  	columns:[],
    defaultColumns: [{
		dataIndex: 'or_id',
		cls: 'x-grid-header-1',
		text: '组织ID',
		width: 0,
		renderer:function(val, meta, record){
			if(val != null && val.toString().trim() != ''){
				return val;
			} 
		}
	},{
		dataIndex: 'or_code',
		cls: 'x-grid-header-1',
		text: '组织编号',
		width: 100
	},{
		dataIndex: 'or_name',
		cls: 'x-grid-header-1',
		text: '组织名称',
		width: 120
	},{
		dataIndex: 'or_headmancode',
		cls: 'x-grid-header-1',
		text: '负责人编号',
		width: 0
	},{
		dataIndex: 'or_headmanname',
		cls: 'x-grid-header-1',
		text: '组织负责人',
		width: 100,
		editor:{
			xtype:'combo',
			field:'or_headmanname',
			queryMode: 'local',
		    displayField: 'em_name',
		  	valueField: 'em_name',
		  	triggerAction:'query',
		  	store:Ext.create('Ext.data.Store',{
		  		  storeId:'empStore', 
                  fields: ['em_code','em_name','em_position','em_mobile','em_email','em_sex'],
                  proxy: {
                     type: 'ajax',
                     async: false,
                      url : basePath + 'hr/employee/getEmployees.action',
				      extraParams: {
				        condition:"nvl(em_class,' ')<>'离职' "
				     },
                     reader: {
                        type: 'json',
                        root: 'employees'
                     }
                  },
                  autoLoad:true   
            }),
		  	listeners:{
		  		select:function(combo,records){
		  			var selected = Ext.getCmp('orgridpanel').selModel.lastSelected;
		  			selected.set('em_defaulthsname', records[0].data.em_position);
		  			selected.set('or_headmancode', records[0].data.em_code);
		  			selected.set('em_mobile', records[0].data.em_mobile);
		  			selected.set('em_email', records[0].data.em_email);
		  			selected.set('em_sex', records[0].data.em_sex);
		  		}
            }
		}
	},{
		dataIndex: 'em_sex',
		cls: 'x-grid-header-1',
		text: '性别',
		width: 60,
		editor:{
			xtype:'combo',
			queryMode: 'local',
		    filterName:'em_sex',
		    editable:false,
		    displayField: 'display',
		    valueField: 'value',
		    store:{data:[{display: "男", value: "男"}, {display: "女", value: "女"}],
		    	   fields: ["display", "value"]
		    }
		}
	},{
		dataIndex: 'em_defaulthsname',
		cls: 'x-grid-header-1',
		text: '负责人岗位',
		width: 120,
		editor:{                                
			xtype:'combo',
			field:'em_defaulthsname',
			queryMode: 'local',
		    displayField: 'jo_name',
		    valueField: 'jo_name',		    
		  	triggerAction:'query',
		  	store:Ext.data.StoreManager.lookup('sys.JobStore')
		}
	},{
		dataIndex: 'em_mobile',
		cls: 'x-grid-header-1',
		text: '手机号',
		width: 120,
		editor:{
			xtype:'textfield',
			field:'em_mobile'
		}
	},{
		dataIndex: 'em_email',
		cls: 'x-grid-header-1',
		text: '邮箱',
		width: 150,
		editor:{
			xtype:'textfield',
			field:'em_email'
		}
	},{
		xtype:'actioncolumn',
		width:80,
		text :'操作',
		items:[{
			iconCls:'button-readed',
			tooltip:'保存',
			handler:function(grid, rowIndex, colIndex,item) {
				var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;
				if((record.data.or_headmanname==null||record.data.or_headmanname=='')&&(record.data.em_sex!=''||record.data.em_mobile!=''||record.data.em_email!=''||record.data.em_defaulthsname!='')){
					showResult('提示','请设置组织负责人!');
					return;
				}
				if(record.data.or_headmanname!=null&&record.data.or_headmanname!=''){
					if(record.data.em_sex==''||record.data.sex==0){
						showResult('提示','性别不能为空!');
						return;
					}
					if(record.data.em_mobile==''||record.data.em_mobile==0){
						showResult('提示','手机号不能为空!');
						return;
					}
					if(record.data.em_email==''||record.data.em_email==0){
						showResult('提示','邮箱不能为空!');
						return;
					}
				}
				if(record.dirty){
					gridpanel.setLoading(true);//loading...
					Ext.Ajax.request({
						url : basePath + 'hr/employee/saveHrOrgAndEmp.action',
						params: {param:unescape(escape(Ext.JSON.encode(record.data))),type:'or'},
						method : 'post',
						callback : function(options,success,response){
							gridpanel.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.exceptionInfo){
								showResult('提示',localJson.exceptionInfo);return;
							}
							if(localJson.success){
								 showResult('提示','修改成功!');
		        				 record.commit();
							}
						}
					});
				}else{
					 showResult('提示','还未修改任何数据!');
				}
				
			}
		},{
			xtype: 'tbtext',
			text: '|'
		},{
			iconCls:'btn-delete',
			tooltip:'删除',
			width:75,
			handler:function(grid, rowIndex, colIndex) {
				Ext.Msg.confirm('删除数据?', '确定要删除当前选中组织?',
					function(btn) {
						if(btn === 'yes') {
							var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;						
							var title=record.get('or_name');
							gridpanel.setLoading(true);
							Ext.Ajax.request({
								url : basePath + 'hr/employee/deleteHrOrgSaas.action',  //更新的delete方法，部门也跟着删除
								params: {
									id: record.get('or_id')
								},
								callback : function(options,success,response){
									gridpanel.setLoading(false);
									var res = new Ext.decode(response.responseText);									
									if(res.exceptionInfo){
										showResult('提示',res.exceptionInfo);return;
									}
									if(res.success){	
										showResult('提示','删除组织('+title+') 成功!');
										grid.getStore().removeAt(rowIndex);
										var orgTree=Ext.getCmp('ortreepanel'),selectionModel = orgTree.getSelectionModel(),
										 selectedList = selectionModel.getSelection()[0];
										 orgTree.getTreeRootNode(0);
										 orgTree.refresh(selectedList);
									}
								}
							});
						}
				});
			}
		}]
	}],
	empColumns: [{
		dataIndex: 'em_id',
		cls: 'x-grid-header-1',
		text: '员工ID',
		width: 0
	},{
		dataIndex: 'em_name',
		cls: 'x-grid-header-1',
		text: '姓名',
		width: 120,
		renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
					}
				},
		editor:{
			xtype:'textfield',
			field:'em_name'
		}
	},{
		dataIndex: 'em_sex',
		cls: 'x-grid-header-1',
		text: '性别',
		width: 60,
		editor:{
			xtype:'combo',
			queryMode: 'local',
			editable:false,
		    filterName:'em_sex',
		    displayField: 'display',
		    valueField: 'value',
		    store:{data:[{display: "男", value: "男"}, {display: "女", value: "女"}],
		    	   fields: ["display", "value"]
		    }
		}
	},{
		dataIndex: 'em_defaultorid',
		cls: 'x-grid-header-1',
		text: '组织id',
		width: 0
	},{
		dataIndex: 'em_defaultorname',
		cls: 'x-grid-header-1',
		text: '组织',
		width: 100
	},{
		dataIndex: 'em_position',
		cls: 'x-grid-header-1',
		text: '岗位',
		width: 120,
		renderer:function(val){
			if(val != null && val.toString().trim() != ''){
				return val;
			} else {
				return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
			}
		},
		editor:{
			xtype:'combo',
			field:'em_position',
			queryMode: 'local',
		    displayField: 'jo_name',
		    valueField: 'jo_name',		    
		  	triggerAction:'query',
		  	store:Ext.data.StoreManager.lookup('sys.JobStore')
		}
	},{
		dataIndex: 'em_code',
		cls: 'x-grid-header-1',
		text: '登录账户',
		width: 0
	},{
		dataIndex: 'em_mobile',
		cls: 'x-grid-header-1',
		text: '手机号',
		width: 100,
		renderer:function(val){
			if(val != null && val.toString().trim() != ''){
				return val;
			} else {
				return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
			}
		},
		editor:{
			xtype:'textfield',
			field:'em_mobile'
		}
	},{
		dataIndex: 'em_email',
		cls: 'x-grid-header-1',
		text: '邮箱',
		width: 150,
		renderer:function(val){
			if(val != null && val.toString().trim() != ''){
				return val;
			} else {
				return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
			}
		},
		editor:{
			xtype:'textfield',
			field:'em_email'
		}
	},{
		xtype:'actioncolumn',
		width:80,
		text :'操作',
		items:[{
			iconCls:'button-readed',
			tooltip:'保存',
			handler:function(grid, rowIndex, colIndex,item) {
				var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;
				if(record.dirty){
					if(Ext.isEmpty(record.data.em_mobile)){ showResult('提示','手机号不能为空');return; }
					if(Ext.isEmpty(record.data.em_email)){ showResult('提示','邮箱不能为空');return; }
					if(Ext.isEmpty(record.data.em_position)){ showResult('提示','岗位不能为空');return; }
					gridpanel.setLoading(true);//loading...
					Ext.Ajax.request({
						url : basePath + 'hr/employee/saveHrOrgAndEmp.action',
						params: {param:unescape(escape(Ext.JSON.encode(record.data))),type:'emp'},
						method : 'post',
						callback : function(options,success,response){
							gridpanel.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.exceptionInfo){
								showResult('提示',localJson.exceptionInfo);return;
							}
							if(localJson.success){
								var me = this,orgTree = Ext.getCmp('ortreepanel'),
								selectionModel = orgTree.getSelectionModel(),
								selectedList = selectionModel.getSelection()[0];
								 if(localJson.data.id){
								 	record.data.em_id=localJson.data.id;
								 	record.data.em_defaultorname=selectedList.data.text;
								 }
								 Ext.data.StoreManager.lookup('empStore').load();
								 showResult('提示','修改成功!');
		        				 record.commit();
							}
						}
					});
				}else{
					showResult('提示','还未修改任何数据!');
				}
			}
		},{
			xtype: 'tbtext',
			text: '|'
		},{
			iconCls:'btn-delete',
			tooltip:'删除',
			width:75,
			handler:function(grid, rowIndex, colIndex) {
				Ext.Msg.confirm('删除数据?', '确定要删除当前选中员工资料?',
					function(btn) {
						if(btn === 'yes') {
							var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;						
							var title=record.get('em_name');
							gridpanel.setLoading(false);
							if(record.get('em_id')==0 || record.get('em_id')==null){
								grid.getStore().removeAt(rowIndex);
								return;
							}
							Ext.Ajax.request({
								url : basePath + 'hr/emplmana/deleteEmployee.action',
								params: {
									id: record.get('em_id')
								},
								callback : function(options,success,response){
									gridpanel.setLoading(false);
									var res = new Ext.decode(response.responseText);
									if(res.exceptionInfo){
										showError(res.exceptionInfo);return;
									}
									if(res.success){	
										showResult('提示','删除员工资料('+title+') 成功!');
										grid.getStore().removeAt(rowIndex);	
										Ext.data.StoreManager.lookup('empStore').load();
									}
								}
							});
						}
				});
			}
		}]
	}],
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
	initComponent : function(){
		this.getdata(null,'or',0);		
		this.callParent(arguments); 
	},
	listeners:{
		afterrender:function(){
			Ext.data.StoreManager.lookup('sys.JobStore').load();
		}
	},
	getdata:function(tree,type,parentId){
		var me=this;
		if(type=='or'){			
			me.setTitle('组织资料');
			if(me.dockedItems.items){
				me.dockedItems.items[2].hide();
			}
			me.setLoading(true);
			Ext.Ajax.request({//拿到tree数据
			    url : basePath + 'hr/hrorg/getHrogrs.action',
			    callback : function(options,success,response){
			    	me.setLoading(false);
			       	if (tree) tree.setLoading(false);
			       	if (!response) return;
			        var res = new Ext.decode(response.responseText);
			       	var store=Ext.create('Ext.data.Store', {
							    fields: ['or_id','or_code','or_name','or_headmancode','or_headmanname','em_mobile','em_defaulthsname',
							    'em_email','em_sex'],
							    data: res.data});
			       	me.reconfigure(store, me.defaultColumns);
			    }
			});
		}else{
			me.setTitle('员工资料');
			me.dockedItems.items[2].show();
			me.setLoading(true);
			Ext.Ajax.request({//拿到tree数据
			    url : basePath + 'hr/employee/getEmployees.action',
			    params: {
			        condition:'em_defaultorid='+parentId
			    },
			    callback : function(options,success,response){
			    	me.setLoading(false);
			    	if (tree) tree.setLoading(false);
			    	if (!response) return;
			        var res = new Ext.decode(response.responseText);
			       	var store=Ext.create('Ext.data.Store', {
							    fields: ['em_id','em_name','em_position','em_defaultorid','em_defaultorname','em_mobile',
							    'em_email','em_sex'],
							    data: res.employees});
			       	me.reconfigure(store, me.empColumns);
			    }
			});
		}
	}
});