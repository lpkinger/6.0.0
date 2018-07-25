Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.MakeChange','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Flow'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			},
    			reconfigure: function(grid, store, columns){
					var detail = getUrlParam('detail');
					gridCondition = getUrlParam('gridCondition');
					if(detail&&!gridCondition){
						me.GridUtil.autoDbfind(grid, 'md_makecode', detail);
					}
				}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				var errs = [];
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['md_newplanenddate'] == null){
    							bool = false;
    							showError('明细表第' + item.data['md_detno'] + '行的计划完工日期为空');return;
    						}else if(item.data['md_newplanbegindate'] == null){
    							bool = false;
    							showError('明细表第' + item.data['md_detno'] + '行的计划开工日期为空');return;
    						}else if(item.data['md_newplanenddate'] < item.data['md_newplanbegindate']){
    							bool = false;
    							showError('明细表第' + item.data['md_detno'] + '行的计划完工日期小于计划开工日期');return;
    						}
    						if(caller == 'MakeChange!OSChange'){
    							if(item.data['md_newqty'] < item.data['ma_madeqty']){
    								bool = false;
    								showError('明细表第' + item.data['md_detno'] + '行的新数量小于委外单的已生成数量');return;
    							}
    							//保存、更新、提交之前:新单价、新税率（如果是RMB）其中一个为0都做提示
    							if(item.data['md_newprice'] == 0 || 
    							    (item.data['md_newcurrency'] =='RMB'&& item.data['md_newtaxrate']==0)){
    								errs.push('行: ' + item.data['md_detno'] + ', ');
    							}
    						}
    					}
    				});  				
    				if(bool){
	    				if(errs.length > 0){
							Ext.MessageBox.show({//关闭前保存修改的数据
								title:'确定保存?',
								msg: errs.join('<br>') + '<br/>中新单价或者新税率为0',
								buttons: Ext.Msg.YESNOCANCEL,
								icon: Ext.Msg.WARNING,
								fn: function(btn){
									if(btn == 'no'){
										return;
									}else if(btn=='yes'){
										me.FormUtil.beforeSave(me);
									}else{
										return;
									}
								}
							});
	    				}else{
    						this.FormUtil.beforeSave(this);
	    				}
    				}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				var errs = [];
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['md_newplanenddate'] == null){
    							bool = false;
    							showError('明细表第' + item.data['md_detno'] + '行的计划完工日期为空');return;
    						}else if(item.data['md_newplanbegindate'] == null){
    							bool = false;
    							showError('明细表第' + item.data['md_detno'] + '行的计划开工日期为空');return;
    						}else if(item.data['md_newplanenddate'] < item.data['md_newplanbegindate']){
    							bool = false;
    							showError('明细表第' + item.data['md_detno'] + '行的计划完工日期小于计划开工日期');return;
    						}
    						if(caller == 'MakeChange!OSChange'){
    							if(item.data['md_newqty'] < item.data['ma_madeqty']){
    								bool = false;
    								showError('明细表第' + item.data['md_detno'] + '行的新数量小于委外单的已生成数量');return;
    							}
    							if(item.data['md_newprice'] == 0 || 
    							    (item.data['md_newcurrency'] =='RMB'&& item.data['md_newtaxrate']==0)){
    								errs.push('行: ' + item.data['md_detno'] + ', ');
    							}
    						}
    					}
    				});
    				if(bool){
    					if(errs.length > 0){
							Ext.MessageBox.show({//关闭前保存修改的数据
								title:'确定更新?',
								msg: errs.join('<br>') + '<br/>中新单价或者新税率为0',
								buttons: Ext.Msg.YESNOCANCEL,
								icon: Ext.Msg.WARNING,
								fn: function(btn){
									if(btn == 'no'){
										return;
									}else if(btn=='yes'){
										me.FormUtil.onUpdate(me);
									}else{
										return;
									}
								}
							});
	    				}else{
    						me.FormUtil.onUpdate(this);
	    				}
    				}				
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('MakeChange!Change', '新增变更单维护', 'jsps/pm/make/makeChange.jsp?whoami='+caller);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var errs = [];
    				if(caller == 'MakeChange!OSChange'){
	    				Ext.each(items, function(item){
	    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
	    							if(item.data['md_newprice'] == 0 || 
	    							    (item.data['md_newcurrency'] =='RMB'&& item.data['md_newtaxrate']==0)){
	    								errs.push('行: ' + item.data['md_detno'] + ', ');
	    							}
	    					}
	    				});
    				}
					if(errs.length > 0){
						Ext.MessageBox.show({//关闭前保存修改的数据
							title:'确定提交?',
							msg: errs.join('<br>') + '<br/>中新单价或者新税率为0',
							buttons: Ext.Msg.YESNOCANCEL,
							icon: Ext.Msg.WARNING,
							fn: function(btn){
								if(btn == 'no'){
									return;
								}else if(btn=='yes'){
									me.FormUtil.onSubmit(Ext.getCmp('mc_id').value);
								}else{
									return;
								}
							}
						});
    				}else{
						me.FormUtil.onSubmit(Ext.getCmp('mc_id').value);
    				}
				}				
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('mc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('mc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					//var status = Ext.getCmp('mc_statuscode');
					//if(status && status.value != 'AUDITED'){
					btn.hide();
					//}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('mc_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('mc_id').value);
				}
			}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
	    this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});