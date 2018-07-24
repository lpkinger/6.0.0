Ext.define('erp.view.sys.hr.OrTreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.orTreePanel', 
	id: 'ortreepanel', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '100%',
	height: '100%',
	expandedNodes: [],
	rootVisible: true, 
	singleExpand: true,
	containerScroll : true, 
	autoScroll: true,
	plugins: [
	Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	})],
    dockedItems: [{
        xtype: 'toolbar',ui: 'footer',	
        items: [{
        		 text:'添加',				
				 tooltip:'添加新组织',
				 iconCls:'btn-add',
				 cls: 'x-btn-gray',
				 handler:function(btn){				 	
				 	var orgTree = Ext.getCmp('ortreepanel');
				 	orgTree.getNewOrg(btn);
				 }
		},{
        		 text:'修改',					
				 tooltip:'修改',
				 disabled:true,
				 id:'updeteHrorgButton',
				 iconCls:'btn-edit',
				 handler:function(btn){
				 	var orgTree = Ext.getCmp('ortreepanel');
				 	orgTree.updateOrg(btn);
				 }
		}]
	}],
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		var me=this;
		me.store= Ext.create('Ext.data.TreeStore', {
	    	root : {
	        	text: 'root',
	        	id: 'root',
	    		expanded: true
	    	}
	    }),
		this.getTreeRootNode(0);
		this.callParent(arguments);
	}, 
	getTreeRootNode: function(parentId){
		var me=this,enname='';
		me.setLoading(true);
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'common/getFieldsData.action',
        	 params: {
	    		 caller: 'Enterprise',
	    		 fields: 'en_name',
	    		 condition: '1=1'
        	 },
        	 callback : function(opt, s, res){
        	 	me.setLoading(false);
        	 	 var r = new Ext.decode(res.responseText);
	    		 if(r.exceptionInfo){
	    		    showError(r.exceptionInfo);return;
	        	 } else if(r.success && r.data){
	        	 	enname='<b>'+r.data.en_name+'</b>';
	        	 }
        	 }
        });
        me.setLoading(true);
        Ext.Ajax.request({//拿到tree数据
			url : basePath + 'hr/employee/getHrOrgTreeSaas.action',
			callback : function(options,success,response){	        		
				me.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.tree){
					var tree = res.tree;
					me.getStore().setRootNode({
				        text:enname,
				        id: 'root',
						expanded: true,
						children: tree
					});
					me.expandAll();
					var record = me.getStore().getNodeById('root');
	   				me.getSelectionModel().select(record);
				} else if(res.exceptionInfo){
			     	showError(res.exceptionInfo);
			    }
	     	}
		});
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
	   itemmousedown: function(selModel, record) {
	  	   var me=this;
           if (!this.flag) {
               return;
           }
           this.flag = false;
           setTimeout(function() {
               me.flag = true;
               me.loadTab(selModel, record);
           }, 20); //防止双击时tree节点重复加载
        },
		itemclick: function(selModel, record){
			var me=this;
			this.flag = true; //防止双击时tree节点重复加载
			if (!this.flag) {
                return;
            }
            this.flag = false;
            setTimeout(function() {
              	me.flag = true;
                me.loadTab(selModel, record);
            }, 20); 
		},
		itemdbclick: function(selModel, record) {
			var me=this;
            if (!this.flag) {
             	 return;
            }
            this.flag = false;
            setTimeout(function() {
                me.flag = true;
                me.loadTab(selModel, record);
            }, 20);
         }
	},
	loadTab:function(selModel, record){
		   var me=this;
		   var parentId=record.data.id;
		   me.setLoading(true);
		   if(record.data.id=='root'){
		   		Ext.getCmp('orgridpanel').getdata(me,'or',record.data.id);
		   		Ext.getCmp('updeteHrorgButton').setDisabled(true);
		   }else{
			   Ext.getCmp('orgridpanel').getdata(me,'em',record.data.id);
			   Ext.getCmp('updeteHrorgButton').setDisabled(false);
		   }
	},
	getNewOrg:function(btn){
		var orgTree=this,selectionModel = orgTree.getSelectionModel(),
		selectedList = selectionModel.getSelection()[0];
		var parentId=selectedList.data.id=='root'?0:selectedList.data.id;
		selectedList.expand(false, true); //展开
		var win =Ext.widget('detailwindow',{
			title:'组织资料',
			id:'detailwindow',
			height:300,
			items:[{
			       xtype:'form',
			       id:'ortreeform',
			       layout : 'column',
			       defaults: {
			    	   xtype:'textfield',
			    	   columnWidth:0.5,
			    	   margin:'5 5 5 5'
					},
			       items:[{
			    	   xtype:'hidden',
			    	   name:'or_id',
			    	   value:'0'
			       },{
			    	   xtype:'hidden',
			    	   name:'or_subof',
			    	   value:parentId
			       },{
						xtype:'textfield',
						name:'or_code',
						fieldLabel:'组织编号',
						value:'',
						readOnly:true
					},{
						xtype:'textfield',
						name:'or_name',
						fieldLabel:'组织名称',
						allowBlank:false,
						afterLabelTextTpl:required,
						renderer:columnRequired,
						value:''
					},{
						xtype:'dbfindtrigger',
						name:'or_headmancode',
						id:'or_headmancode',
						fieldLabel:'组织负责人号',
						editable:false,
						value:''
					},{
						xtype:'textfield',
						name:'or_headmanname',
						id:'or_headmanname',
						fieldLabel:'组织负责人名',
						readOnly:true,
						value:''
					},{
						xtype:'textarea',
						name:'or_remark',
						fieldLabel:'职能',
						columnWidth:1,
						height:150,
						value:''
					}]
			
			}],
			closeAction: 'destroy',
			layout : 'fit',
			buttons: [{
				text: '保存',
				handler: function(btn) {
					var ortreeform = Ext.getCmp('ortreeform');
					var orname=btn.ownerCt.ownerCt.down('textfield[name=or_name]').value;
					if(orname==null || orname==''){
						showResult('提示','组织名称必须填写!!!');
						return;
					}
					var formStore =unescape(escape(Ext.JSON.encode(ortreeform.getValues())));					
					Ext.Ajax.request({
						url:basePath+'hr/employee/saveHrOrgSaas.action',
						params:{
							formStore:formStore
						},
						method:'post',
						callback:function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.success){
								showResult('提示','保存成功');
								btn.up('window').close();
							}else{
								showResult('提示',res.exceptionInfo);
								return;
							}
						}
					});
				}
			},{
				text: '清空',
				handler: function() {
					this.up('window').queryById('ortreeform').getForm().reset();
				}
			}],
			listeners:{
				close:function(){
					orgTree.getTreeRootNode(0);
				}
			}
		});
		win.showRelyBtn(win,btn);
	 },
	 updateOrg:function(btn){
		var me = this;
	 	var orgTree=this,selectionModel = orgTree.getSelectionModel(),
		selectedList = selectionModel.getSelection()[0];
		var orid=selectedList.data.id=='root'?0:selectedList.data.id;
		me.setLoading(true);
		Ext.Ajax.request({
			url : basePath + "common/getFieldsData.action",
			params:{
					caller:'HRORG',
					fields:'or_code,or_name,or_headmancode,or_headmanname,or_remark',
					condition:'or_id='+orid
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				if(res.data){
					orcode = res.data.or_code;
					orname = res.data.or_name;
					orheadmancode = res.data.or_headmancode;
					orheadmanname = res.data.or_headmanname;
					orremark = res.data.or_remark;
					if(orid!=0){
						var win=Ext.widget('detailwindow',{
							title:'组织资料',
							height:300,
							items:[{
							       xtype:'form',
							       id:'ortreeform2',
							       layout : 'column',
							       defaults: {
							    	   xtype:'textfield',
							    	   columnWidth:0.5,
							    	   margin:'5 5 5 5'
									},
							       items:[{
							    	   xtype:'hidden',
							    	   name:'or_id',
							    	   value:orid
							       },{
										xtype:'textfield',
										name:'or_code',
										fieldLabel:'组织编号',
										readOnly:true,
										value:orcode
									},{
										xtype:'textfield',
										name:'or_name',
										fieldLabel:'组织名称',
										allowBlank:false,
										afterLabelTextTpl:required,
										value:orname
									},{
										xtype:'dbfindtrigger',
										id:'or_headmancode',
										name:'or_headmancode',
										fieldLabel:'组织负责人号',
										editable:false,
										value:orheadmancode
									},{
										xtype:'textfield',
										id:'or_headmanname',
										name:'or_headmanname',
										fieldLabel:'组织负责人名',
										readOnly:true,
										value:orheadmanname
									},{
										xtype:'textarea',
										name:'or_remark',
										fieldLabel:'职能',
										height:150,
										columnWidth:1,
										value:orremark
									}]
							
							}],
							closeAction: 'destroy',
							layout : 'fit',
							buttons: [{
								text: '保存',
								handler: function(btn) {
									var ortreeform = Ext.getCmp('ortreeform2');
									var bool = this.up('window').queryById('ortreeform2').getForm().isDirty();
									if(!bool){
										showResult('提示','未修改数据，无法更新!');
										return;
									}
									Ext.each(ortreeform.items.items, function(item){
										if(item.xtype=='checkbox'){
											item.dirty=true;
											if(item.checked){
												item.inputValue='1';					
											}else item.inputValue='0';
										}
									});
									var formStore =unescape(escape(Ext.JSON.encode(ortreeform.getValues())));	
									Ext.Ajax.request({
										url:basePath+'hr/employee/updateHrOrgSaas.action',
										params:{
											formStore:formStore
										},
										method:'post',
										callback:function(options,success,response){
											var res = new Ext.decode(response.responseText);
											if(res.success){
												showResult('提示','修改成功');
												btn.up('window').close();
											}else{
												showResult('提示',res.exceptionInfo);
												return;
											}
										}
									});
								}
							},{
								text: '关闭',
								handler: function(btn) {
									btn.up('window').close();
								}
							}],
							listeners:{
								close:function(){
									orgTree.getTreeRootNode(0);
								}
							}
						});
						win.showRelyBtn(win,btn);
					}
				}
			}
		});
	 },
	 refresh:function(record){
		var me=this,parentId=0;
		if(record.get('id')!='root'){
			parentId=record.data.id;
		}
		me.setLoading(true);
		Ext.Ajax.request({//拿到tree数据
			url: basePath + 'hr/employee/getHrOrgStrTree.action',
			params: {
				parentid: parentId
			},
			callback: function(options, success, response) {
				 me.setLoading(false);
				 var res = new Ext.decode(response.responseText);
				 if (res.tree&&res.tree.length>0) {
				  	var tree = res.tree;
				    record.removeAll();
					record.appendChild(tree);
					record.expand(false); //展开
				 } else if (res.exceptionInfo) {
				    showError(res.exceptionInfo);
				 }
			}
		});
	}
});