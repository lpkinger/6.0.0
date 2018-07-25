Ext.define('erp.view.hr.employee.OrgArcSet', {
	extend: 'Ext.tree.Panel',
	id:'orgarcset',
	alias: 'widget.orgarcset',
	useArrows: true,//true，在tree中使用Vista-style样式的箭头。
	rootVisible: false,
	multiSelect: true,//如果设置为“true”，允许combo域同时保持多个值，并允许从下来列表选多个值。 combo的文本域将显示所有已选值，用delimiter分隔。
	title: '组织资料',
	viewConfig: {stripeRows:true},
	initComponent: function(config) {
		var me=this;
		me.store=Ext.create('Ext.data.TreeStore', {
			storeId: 'hrorgstore',
			fields: [{name:"or_id",type:"number"},
			         {name:"or_code",type:"string"},
			         {name:"or_name",type:"string"},
			         {name:"agentuu",type:"string"}],
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
			text:'代理商UU',
			width:0,
			sortable:true,
			dataIndex:'agentuu',
			value:enUU,
			editor:{
				xtype:'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false
			}
		},{
			xtype: 'actioncolumn',
			width: 30,
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
		this.getTreeGridNode("or_code='"+orcode+"'");
	},
    getTreeGridNode: function(condition){
    	var me = this;
    	Ext.Ajax.request({//拿到tree数据
    		url : basePath + 'hr/getChildTreeNode.action?condition='+condition,
    		callback : function(options,success,response){
    			var res = new Ext.decode(response.responseText);
    			if(res.result){
    				var tree = res.result;
    				Ext.each(tree, function(t){
    					t.or_id = t.id;
    					if(t.data){
        					t.or_code=t.data.or_code;
        					t.or_name=t.data.or_name;
        					t.agentuu=t.data.agentuu;//
        					t.data = null;
    					}
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
    			agentuu:record.data.agentuu,//
    	};
    	Ext.Ajax.request({
    		url:basePath+'hr/employee/updateHrOrgById.action',
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