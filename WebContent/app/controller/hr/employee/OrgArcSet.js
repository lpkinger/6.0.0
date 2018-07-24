Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.OrgArcSet', {
	extend: 'Ext.app.Controller',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	views:['hr.employee.OrgArcSet','hr.employee.StaffInfo','core.trigger.DbfindTrigger'],
	init:function(){
		var me=this;
		this.control({
			'orgarcset': {
				itemmousedown:function(selModel, record){
					me.reloadJob(record);
				},
				itemmouseenter: me.showActions,
				itemmouseleave: me.hideActions,
				beforeitemmouseenter:me.showActions,
				removeclick:me.handleRemoveClick,
				speexpandclick:me.handleSpeExpandClick
			},
			'button[itemId=addOrg]':{//menuitem
				click:me.handleAddOrgClick
			},
			'button[itemId=addemployee]':{
				click:me.handleaddEmployeeClick
			},
			'button[itemId=saveemployee]':{
				click:me.handleSaveEmployeeClick
			},
		});
	},
	handleAddOrgClick: function(component, e) {
		this.addOrg(true,component);
	},
	handleaddEmployeeClick:function(component,e){
		this.addEmployee();
	},
	handleSaveEmployeeClick:function(component,e){
		this.saveEmplyoee();
	},
	saveEmplyoee:function(){
		var employeegrid=Ext.getCmp('staffinfo'),me=this;//jobgrid
		if(employeegrid.isVal()){
			employeegrid.store.sync({
				failure:function(){
					showError('手机号不允许修改或者手机号已经存在不允许保存!');
				},
				success:function(){
					showResult('提示','保存成功!');
					me.reloadJob();
				}
			});
		}else showResult('提示','有必填项未填写无法保存!');
		
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
	},
/*	addEmployee:function(){
		var me = this,
		employeegrid=Ext.getCmp('staffinfo'); edit = employeegrid.cellEditingPlugin;
		edit.cancelEdit();
		employeegrid.store.insert(0, {});
		edit.startEditByPosition({
			row: 0,
			column: 1
		});
	},*/
	addEmployee:function(){
		var me = this,
		orgTree = Ext.getCmp('orgarcset'),
		cellEditingPlugin = orgTree.cellEditingPlugin,
		selectionModel = orgTree.getSelectionModel(),
		selectedList = selectionModel.getSelection()[0];
		if(!selectedList) {
			showResult('提示','请先选择需要添加员工的所属组织!');
		    return;
		}
		var rec ={
			em_id: '',
			em_code: '',
			em_name: '',
			em_defaulthscode:'',
//			em_defaulthsname:'',//em_defaultorname
//			em_defaultorname:selectedList.data.or_name,
			em_position:'', 
			em_mobile:'',
			em_email:'',
			em_defaultorid :selectedList.get('or_id')
		}, employeegrid=Ext.getCmp('staffinfo'); edit = employeegrid.cellEditingPlugin;
		edit.cancelEdit();
		employeegrid.store.insert(0, rec);
		edit.startEditByPosition({
			row: 0,
			column: 1
		});
	},
	addOrg:function(leaf,btn,type){
		var me = this,
		orgTree = Ext.getCmp('orgarcset'),
		cellEditingPlugin = orgTree.cellEditingPlugin,
		selectionModel = orgTree.getSelectionModel(),
		selectedList = selectionModel.getSelection()[0];
		var parentId=selectedList && !type?selectedList.get('or_id'):null;
		if(parentId==null){
			showResult('提示','不允许添加顶级组织!');
			return;
		}
		parentList=parentId==0?orgTree.getRootNode():selectedList;
		newOrg =this.getNewOrg(parentId,btn);
		expandAndEdit = function() {
			if(parentList.isExpanded()) {
				selectionModel.select(newOrg);
				me.addedNode = newOrg;
				cellEditingPlugin.startEdit(newOrg, 0);
			} else {
				orgTree.on('afteritemexpand', function startEdit(list) {
					if(list === parentList) {
						selectionModel.select(newList);
						me.addedNode = newList;
						cellEditingPlugin.startEdit(newOrg, 0);
						orgTree.un('afteritemexpand', startEdit);
					}
				});
				parentList.expand();
			}
		};
		parentList.appendChild(newOrg);
		orgTree.getStore().sync();
		if(orgTree.getView().isVisible(true)) {
			expandAndEdit();
		} else {
			orgTree.on('expand', function onExpand() {
				expandAndEdit();
				listTree.un('expand', onExpand);
			});
			orgTree.expand();
		}
	},
	getNewOrg:function(parentId,btn){
		var org=new Object();
		parentId=parentId?parentId:0;
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'hr/addOrg.action',//    hr/addOrg.action     //hr/addOrgByParent.action
			params: {
				parentId: parentId
			},
			async:false,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.org){
					org=res.org;
					org.loaded=true;
					org.id=org.or_id;
				} 
				btn.setDisabled(false);
			}
		});
		return org;
	},
	loadNode: function(record){
		var me = this;
		if (record.data['or_id']) {
			if(record.isExpanded() && record.childNodes.length > 0){
				me.flag = true;
			} else {
				if(record.childNodes.length == 0){
					Ext.Ajax.request({//拿到tree数据
						url : basePath + 'hr/getChildTreeNode.action',
						params: {
							condition:'or_subof='+record.data['id']
						},
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.result && res.result.length>0){
								var tree = res.result;
								Ext.each(tree, function(t){
									t.or_id = t.id;
									t.or_code=t.data.or_code;
									t.or_name=t.data.or_name;
									t.agentuu=t.data.agentuu;///////
									t.leaf=false;
									t.data = null;
								});									
								record.appendChild(tree);				        	
								Ext.each(record.childNodes, function(){
									this.dirty = false;
								});				        		
							} else if(res.exceptionInfo){
								showError(res.exceptionInfo);
							}
						}
					});
				}
			}
		}
	},
	reloadJob:function(record){
        if(!record){
    		var orgTree = Ext.getCmp('orgarcset'),
    		selectionModel = orgTree.getSelectionModel(),
    		record = selectionModel.getSelection()[0];
        }
		var jobgrid=Ext.getCmp('staffinfo');
		jobgrid.setTitle('员工资料 <span style="color:gray">('+record.get('or_name')+')</span>');
		jobgrid.getStore().load({params:{
			condition:'em_defaultorid = '+record.get('or_id')
		}});
	},
	handleSpeExpandClick:function(record){
		this.loadNode(record);
		var treegrid = Ext.getCmp('orgarcset');
		treegrid.selModel.select(record);
		this.reloadJob(record);

	},
	changeInputValue:function(field,value){
		showResult('提示','修改成功!');
		field.originalValue=value;
		
	},
	showActions: function(view, list, node, rowIndex, e) {
		var icons = Ext.DomQuery.select('.x-action-col-icon', node),record=view.getRecord(node);
		Ext.each(icons, function(icon){
			Ext.get(icon).removeCls('x-hidden');
		});
	},
	hideActions: function(view, list, node, rowIndex, e) {
		var icons = Ext.DomQuery.select('.x-action-col-icon', node),record=view.getRecord(node);
		Ext.each(icons, function(icon){
			if(icon.getAttribute('src')==basePath+'jsps/sys/images/deletetree.png')
				Ext.get(icon).addCls('x-hidden');
		});
	},
	handleRemoveClick: function(view, rowIndex, colIndex, column, e) {
		var record=view.getRecord(view.findTargetByEvent(e)),title=record.get('or_name'), orgTree = Ext.getCmp('orgarcset'),
		selModel = orgTree.getSelectionModel();
		Ext.Msg.confirm('删除数据?', '确定要删除当前选中组织 ('+title+')?',
				function(choice) {
			if(choice === 'yes') {
				Ext.Ajax.request({
					url : basePath + 'hr/employee/deleteHrOrgById.action',
					params: {
						id: record.get('id')
					},
					async:false,
					callback : function(options,success,response){
						var res = new Ext.decode(response.responseText);
						if(res.success){
							showResult('提示','删除组织('+title+') 成功!');
							record.parentNode.removeChild(record);
							if (!selModel.hasSelection()) {
								selModel.select(0);
							}				                 
						} 


					}
				});
			}
		});   
	}
});