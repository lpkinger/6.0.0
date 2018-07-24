Ext.define('erp.view.oa.flow.button.SaveButton',{ 
		extend: 'Ext.Button', 
		alias: 'widget.SaveButton',
		text: '保存',
		id:'save',
		formBind: true,
    	cls: 'x-btn-gray',
    	margin:'0 5 0 0',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(btn){
			var me = this;
			var form = btn.ownerCt.ownerCt;
			if(btn._type == 'Turn'){	//普通保存
				var _url = 'oa/flow/save.action';
				formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
				var id = formCondition.split('=')[1];
				form.beforeSave(form,_url,id,me._id);
			}else if(btn._type == 'Task'){	 //派生任务
				var _url = '';
				warnMsg('是否确定发起任务？', function(btn){
					if(btn == 'yes'){
						form.saveTask(form);
						var savebtn = Ext.getCmp('save');
						var tab = savebtn.ownerCt.ownerCt.ownerCt;
						tab.remove(tab.getActiveTab());
						//显示tbar
						var panels = tab.items.items;
						Ext.each(panels, function(panel){
							if(panel._first){
								if(panel.dockedItems&&panel.dockedItems.items){
									panel.dockedItems.items[0].show();
								}
							}
						});
						tab.doLayout();
						tab.setActiveTab(0);
					} else {
						return;
					}
				});
			}else if(btn._type == 'Flow'){	 //派生流程
				SaveTwoButton('是否确定派生？', function(btn){
					if(btn == 'yes'){
						var _url = 'oa/flow/saveFlow.action';
						formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
						var id = formCondition.split('=')[1];
						form.saveInstance(form,_url,id,me._id);
						var savebtn = Ext.getCmp('save');
						var tab = savebtn.ownerCt.ownerCt.ownerCt;
						tab.remove(tab.getActiveTab());
						//显示tbar
						var panels = tab.items.items;
						Ext.each(panels, function(panel){
							if(panel._first){
								if(panel.dockedItems&&panel.dockedItems.items){
									panel.dockedItems.items[0].show();
								}
							}
						});
						tab.doLayout();
						tab.setActiveTab(0);
					} else {
						return;
					}
				});
			}else if(btn._type == 'Update'){	 //参与者更新
					var _url = 'oa/flow/update.action';
					form.update(form);
					var savebtn = Ext.getCmp('save');
					var tab = savebtn.ownerCt.ownerCt.ownerCt;
					tab.remove(tab.getActiveTab());
					//显示tbar
					var panels = tab.items.items;
					Ext.each(panels, function(panel){
						if(panel._first){
							if(panel.dockedItems&&panel.dockedItems.items){
								panel.dockedItems.items[0].show();
							}
						}
					});
					tab.doLayout();
					tab.setActiveTab(0);
			}
		}
	});