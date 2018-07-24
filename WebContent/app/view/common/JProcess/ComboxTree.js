Ext.define('erp.view.common.JProcess.ComboxTree',{ 
	extend: 'Ext.form.field.ComboBox', 
	alias: 'widget.treeCombox',
	store : new Ext.data.ArrayStore({fields:[],data:[[]]}),
	editable : false,
	width:350,
	labelAlign:'right',
	readOnly:false,
	_idValue : null,
	_txtValue : null,
	fieldLabel: '选择办理人',
	queryMode: 'local',
	emptyText :'下拉并双击选择',
	fieldStyle:'background:#FFFAFA;color:#515151;',
	initComponent : function(){

		var combo = this;
		this.callParent(arguments);
		this.treeRenderId = Ext.id();
		//必须要用这个定义tpl
		this.tpl = new Ext.XTemplate('<tpl for="."><div style="height:100' + 'px;"><div id="' + this.treeRenderId+ '"></div></div></tpl>');
		var treeObj = new Ext.tree.Panel({
			border : false,
			id:'tree',
			autoScroll : true,
			rootVisible: false, 
			listeners: {
				itemclick: {
			            fn: function(){}
			        }
			  },
		
		});
		/*treeObj.on('itemclick',function(view,rec){*/
		treeObj.on('itemdblclick',function(view,record,item,index,e){
			/*treeObj.fireEvent('itemclick',null);*/
			/*console.log(Ext.getCmp('tree').getSelectionModel())
			console.log(e);
			console.log(record);*/
			if(record){
				combo.setValue(this._txtValue = record.get('text'));
				combo._idValue = record.get('text');
				combo.collapse();
			}
		});

		this.on({
			'expand' : function(){
				if(!treeObj.rendered&&treeObj&&!this.readOnly){
					var me = this;
					Ext.Ajax.request({//拿到tree数据
			        	/*url : basePath + 'comboxTree.action',*/
						url : basePath + 'common/getOrgAssignees.action',
			        	callback : function(options,success,response){
			        		var res1 = response.responseText;
			        		var res =Ext.decode(res1);
			        		if(res.tree){
			        			var tree = Ext.decode(res.tree);
			        			for(x in tree){    
			        				delete tree[x].id;
			        				if(tree[x].children){
			        					var children = tree[x].children;
			        					for( y in children){
			        						delete children[y].id;
			        					}
			        				}
			        				
			        			}
			        			
			        			Ext.getCmp('tree').getStore().setRootNode({
			                		text: 'root',
			                	    id: 'root',
			                		expanded: true,
			                		children: tree
			                	});
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        		Ext.defer(function(){
								/*console.log(me.xtype);*/
								treeObj.render(me.treeRenderId);
								
							},300,me);
			        	}
			        });
					
				}
			}
		});

},
	getValue : function(){//获取id值
		return this._idValue;
	},
	getTextValue : function(){//获取text值
		return this._txtValue;
	},
	setLocalValue : function(txt,id){//设值
		this._idValue = id;
		this.setValue(this._txtValue = txt);
	}
});
	
    
    
