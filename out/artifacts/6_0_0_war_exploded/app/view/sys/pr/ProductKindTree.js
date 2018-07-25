Ext.define('erp.view.sys.pr.ProductKindTree', {
	extend: 'Ext.tree.Panel',
	alias: 'widget.productkindtree',
	id:'productkindtree',
	useArrows: true,
	rootVisible: false,
	multiSelect: true,
	columnLines: true,
	viewConfig: {stripeRows:true},
	//forceFit:true,
	dockedItems: [/* {
	      		xtype: 'toolbar',
	      		dock: 'bottom',
	      		ui: 'footer',
	      		layout: {
	      			pack: 'center'
	      		},
	      		items: [{
	      			minWidth: 80,
	      			text: 'Save'
	      		},{
	      			minWidth: 80,
	      			text: 'Cancel'
	      		}]
	      	}, */{
	      		xtype: 'toolbar',
	      		ui: 'footer',
	      		items: [{
	      			text:'添加',	      
	      			tooltip:'添加新记录',
	      			iconCls:'btn-add',
	      			menu: {
	      				items: [{
	      					text: '顶层种类',
	      					iconCls:'btn-add',
	      					itemId:'topProductKind'
	      				},{
	      					text: '添加种类',
	      					iconCls: 'btn-add',
	      					itemId: 'addProductKind'
	      				}]
	      			}
	      		},/* '-', {
	      			text:'修改',
	      			itemId: 'sa_updateButton',
	      			tooltip:'修改选中行',
	      			iconCls:'btn-edit',
	      			disabled: true	
	      		},'-',{
	      			itemId: 'sa_removeButton',
	      			text:'删除',
	      			tooltip:'删除选中行',
	      			iconCls:'btn-delete',
	      			disabled: true
	      		}, '-', {
	      			text:'帮助',
	      			iconCls:'btn-help',
	      			tooltip:'帮助简介'
	      		}*/]
	      	}],
	      	initComponent: function(config) {
	      		var me=this;
	      		me.columns=[{
	      			xtype: 'treecolumn',
	      			text: '种类编号',
	      			width: 200,
	      			sortable: true,
	      			dataIndex: 'pk_code',
	      			editor: {
	      				xtype: 'textfield',
	      				selectOnFocus: true,
	      				allowOnlyWhitespace: false
	      			}
	      		},{
	      			text: '种类名称',
	      			width: 150,
	      			dataIndex: 'pk_name',
	      			sortable: true,
	      			editor: {
	      				xtype: 'textfield',
	      				selectOnFocus: true,
	      				allowOnlyWhitespace: false
	      			}
	      		},{text:'ID',width:0,dataIndex:'pk_id'},{
	      			xtype: 'actioncolumn',
	      			width: 40,
	      			/*icon:'http://localhost:8080/ERP/jsps/sys/images/deletetree.png',basePath+*/
	    			iconCls: 'btn-delete',//'x-hidden'
	      			renderer :function(val, meta, record){
	      				meta.tdCls = record.get('cls');
	      				meta.tdAttr = record.get('leaf')?'data-qtip="新增'+record.get('text')+'"':'data-qtip="删除种类"';
	      			},
	      			handler: Ext.bind(me.handleRemoveClick, me)
	      		}];
		      	me.store=Ext.create('Ext.data.TreeStore', {
		      		storeId: 'hrorgstore',
		      		fields: [{"name":"pk_id","type":"number"},
		      		         {"name":"pk_code","type":"string"},
		      		         {"name":"pk_name","type":"string"}],
		      		         root : {
		      		        	 text: 'root',
		      		        	 id: 'root',
		      		        	 expanded: true
		      		         },
		      		         listeners:{
		      		        	 beforeexpand:Ext.bind(me.handleSpeExpandClick, me)			        
		      		         } 
		      	});
	      		me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing',{
	      			clicksToEdit:1,
	      			listeners:{
	      				'edit':function(editor,e,Opts){
	      					if(e.record.parentNode.data.id=='root'){
	      						var pk_subof='0';
	      					}
	      					else{
	      						var pk_subof=e.record.parentNode.data.id;
	      					}
	      					var record=e.record,update={
	      							pk_id:record.data.pk_id,
	      							pk_code:record.data.pk_code,
	      							pk_name:record.data.pk_name,
	      							pk_subof:pk_subof
	      					};
	      					if(e.originalValue!=e.value && e.value){
	      						Ext.Ajax.request({
	      							url:basePath+'scm/sale/updateProductKind.action',
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
	      				}
	      			}
	      		})];
	      		this.callParent(arguments);
	      		this.getTreeGridNode({parentid: 0});
	      	},
	      	getTreeGridNode: function(param){
	      		var me = this;
	      		Ext.Ajax.request({//拿到tree数据
	      			url : basePath + 'scm/product/getProductKindTree.action',
	      			params: param,
	      			callback : function(options,success,response){
	      				var res = new Ext.decode(response.responseText);			
	      				if(res.tree){
	      					var tree = res.tree;
	      					Ext.each(tree, function(t){
	      						t.pk_id = t.data.pk_id;
	      						t.pk_code=t.data.pk_code;
	      						t.pk_name=t.data.pk_name;
	      						t.leaf=false;
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
	         }
});
