Ext.define('erp.view.sys.hr.OrgTreePanel', {
	extend: 'Ext.tree.Panel',
	id:'orgtree',
	alias: 'widget.orgtreepanel',
	//collapsible: true,
	useArrows: true,
	rootVisible: false,
	multiSelect: true,
	title: '组织资料',
	viewConfig: {stripeRows:true},
	initComponent: function(config) {
		var me=this;
		me.store=Ext.create('Ext.data.TreeStore', {
			storeId: 'hrorgstore',
			fields: [{name:"or_id",type:"number"},
			         {name:"or_code",type:"string"},
			         {name:"or_name",type:"string"},
			         {name:'or_departmentcode',type:"string"},
			         {name:"or_department",type:"string"}],
			         root : {
			        	 text: 'root',
			        	 id: 'root',
			        	 expanded: true
			         },
			         listeners:{
			        	 beforeexpand:Ext.bind(me.handleSpeExpandClick, me)			        
			         } 
		});
		me.columns=[{
			xtype: 'treecolumn',
			text: '组织编号',
			width: 200,
			sortable: true,
			dataIndex: 'or_code',
			//locked: true,
			editor: {
				xtype: 'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false
			}
		},{
			text: '组织名称',
			width: 150,
			dataIndex: 'or_name',
			sortable: true,
			flex:1,
			editor: {
				xtype: 'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false
			}
		},{
			text:'部门编号',
			width: 150,
			dataIndex:'or_departmentcode',
			sortable:true,
			editor: {
				xtype:'dbfindtrigger',
				dbfind:'Department|dp_code',
				dbCaller:'HrOrg',
				selectOnFocus: true,
				allowOnlyWhitespace: false,
				autoDbfind:false,
				listeners:{
					'aftertrigger':function(trigger){
						/**
						 * 放大镜触发编辑事件有问题
						 */
						var orgTree=trigger.owner,selectionModel = orgTree.getSelectionModel(),
						record = selectionModel.getSelection()[0];
						if(trigger.wasDirty){
							orgTree.updateRecord(record);
						}
					}
				}
			},
			flex:1
		},{
			text:'部门名称',
			width: 150,
			dataIndex:'or_department',
			sortable:true,
			editor: {
				xtype: 'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false
			},
			flex:1
		},{
			xtype: 'actioncolumn',
			width: 40,
			icon:basePath+'jsps/sys/images/deletetree.png',
			iconCls: 'x-hidden',
			renderer :function(val, meta, record){
				meta.tdCls = record.get('cls');
				meta.tdAttr = record.get('leaf')?'data-qtip="新增'+record.get('text')+'"':'data-qtip="删除组织"';
			},
			handler: Ext.bind(me.handleRemoveClick, me)
		},{
			text: '组织ID',
			width: 0,
			dataIndex: 'or_id',
			//sortable: true
		}];
		me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing',{
			clicksToEdit:1,
			listeners:{
				'edit':function(editor,e,Opts){
					if(e.originalValue!=e.value && e.value){
						me.updateRecord(e.record);
					}
				}
			}
		})];
		this.callParent(arguments);
		this.getTreeGridNode({parentId: 0});
	},
	dbfinds:[{dbGridField:'dp_code',field:'or_departmentcode'},
	         {dbGridField:'dp_name',field:'or_department'}],
	         getTreeGridNode: function(param){
	        	 var me = this;
	        	 Ext.Ajax.request({//拿到tree数据
	        		 url : basePath + 'hr/getTreeNode.action',
	        		 params: param,
	        		 callback : function(options,success,response){
	        			 var res = new Ext.decode(response.responseText);			
	        			 if(res.result){
	        				 var tree = res.result;
	        				 Ext.each(tree, function(t){
	        					 t.or_id = t.id;
	        					 t.or_code=t.data.or_code;
	        					 t.or_name=t.data.or_name;
	        					 t.or_department=t.data.or_department;
	        					 t.or_departmentcode=t.data.or_departmentcode;
	        					 t.data = null;
	        				 });
	        				 me.store.setRootNode({
	        					 text: 'root',
	        					 id: 'root',
	        					 expanded: true,
	        					 children: tree
	        				 });
	        				 Ext.each(me.store.tree.root.childNodes, function(){
	        					 this.dirty = false;
	        				 });
	        			 } else if(res.exceptionInfo){
	        				 showError(res.exceptionInfo);
	        			 }
	        		 }
	        	 });
	         },
	         handleRemoveClick: function(gridView, rowIndex, colIndex, column, e) {
	        	 this.fireEvent('removeclick', gridView, rowIndex, colIndex, column, e);
	         },
	         handleSpeExpandClick: function(record) {
	        	 if(record.get('id')!='root'){
	        		 this.fireEvent('speexpandclick', record);
	        	 }

	         },
	         updateRecord:function(record){
	        	 var update={
	        			 or_id:record.data.or_id,
	        			 or_code:record.data.or_code,
	        			 or_name:record.data.or_name,
	        			 or_department:record.data.or_department,
	        			 or_departmentcode:record.data.or_departmentcode
	        	 };

	        	 Ext.Ajax.request({
	        		 url:basePath+'hr/employee/updateHrOrg.action',
	        		 params: {
	        			 formStore:unescape(escape(Ext.JSON.encode(update)))
	        		 },
	        		 method : 'post',
	        		 callback : function(options,success,response){
	        			 var local=Ext.decode(response.responseText);
	        			 if(local.success) {
	        				 showResult('提示','修改成功!');
	        				 record.commit();
	        			 }else {
	        				 showResult('提示',local.exceptionInfo);
	        			 }
	        		 }
	        	 });
	         }
});
