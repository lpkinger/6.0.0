Ext.QuickTips.init();
Ext.define('erp.controller.ma.DataList', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.DataList','core.form.Panel','ma.MyDataList','core.button.CopyAll',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.Sync','core.button.ComboButton',
   		'core.button.Upload','core.button.Update','core.button.Delete','core.button.DeleteDetail',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.toolbar.Toolbar', 'core.grid.YnColumn'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSyncButton': {
    			afterrender: function(btn){
    				if(isSaas){btn.hide();};
    				btn.autoClearCache = true;
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var f=me.check('save');
    				if(f){
	    				this.FormUtil.beforeSave(me);				
    				};	
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var f=me.check('update');
    				if(f){
    					me.save();
    				}
    			}
    		},
    		'erpCopyButton': {
    			click: function(btn){
    				me.copyConfig();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addDataList', '新增DataList配置', 'jsps/ma/dataList.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(me);
    			}
    		},
    		'erpDeleteButton' :{
    			click:function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('dl_id').value);
    			}
    		},
    		'mydatalist': {
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			},
    			select:function(selModel,record){
 					var grid=selModel.view.ownerCt.ownerCt;
    				if(record && record.data.dld_fieldtype == 'C'){
    					grid.down('erpComboButton').setDisabled(false);
    				}else {
    					grid.down('erpComboButton').setDisabled(true);
    				}
    			}
    		},
    		'erpFormPanel textfield[name=dl_caller]': {
    			change: function(field){//主表dl_caller值变更时,对应从表字段也变更
    				var grid = Ext.getCmp('grid');
					Ext.Array.each(grid.store.data.items, function(item){
						item.set('dld_caller',field.value);
					});
    			}
    		},
    		'erpFormPanel textfield[name=dl_tablename]': {
    			change: function(field,newVal){//主表dl_tablename值变更时,对应从表字段也变更
    				var grid = Ext.getCmp('grid');
					Ext.Array.each(grid.store.data.items, function(item){
						item.set('dld_table',newVal);
					});
    			}
    		},
    		'erpComboButton': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected; 
    				if(record) {
    					if(record.data.dld_fieldtype == 'C') { 	
	    					btn.comboSet(whoami, record.data.dld_field);
	    				}
    				}
    			}
    		},
    		'#erpSetComboButton': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt,
    					record = grid.selModel.lastSelected;
    				if(record && record.get('dld_fieldtype') == 'C') {
    					warnMsg('确定重置下拉框数据?', function(b){
    						if(b == 'ok' || b == 'yes') {
    							Ext.Ajax.request({
    								url: basePath + 'ma/resetCombo.action',
    								params: {
    									caller: whoami,
    									field: record.get('dld_field')
    								},
    								callback: function(opt, s, r) {
    									if(s) {
    										var rs = Ext.decode(r.responseText);
    										if(rs.error) {
    											alert(rs.error);
    										} else {
    											alert('设置成功!');
    										}
    									}
    								}
    							});
    						}
    					});
    				}
    			}
    		}
    	});
    },
	check:function(type){//检查table主键中字段是否配置
	    var keyField=Ext.getCmp('dl_keyfield').value;
	 	var keyArr=new Array();
	 	if(keyField != null && keyField != ''){
			if(keyField.indexOf('+') > 0) {
			    var arr = keyField.split('+');
			    Ext.Array.each(arr, function(r){
					ff = r.split('@');
					keyArr.push(ff[1]);
				});
			} else {
			    keyArr.push(keyField);
			}
		}
	    var items=Ext.getCmp('grid').store.data.items,dd= new Array(),flag=true;
	    Ext.Array.each(items, function(item){
			d = item.data;
			if(type=='save'){
	    		if(!Ext.isEmpty(d['dld_field'])){
					dd.push(d);
				}
		    }else if(type=='update'){
		    	if(d['deploy'] == true){
		    		dd.push(d);
		    	}
		    }
		});
		if(dd.length > 0) {
			Ext.Array.each(keyArr,function(key){
			    flag=false;
				for(var i=0;i<dd.length;i++){
					if(key==dd[i]['dld_field']){
						flag=true;
						break;
					}
				}
				if(!flag){
					showError('未配置table主键字段：'+key);
					return flag;
				}
			});
		} else {
			showError('请至少配置一个有效字段!');
			return false;
		}
		return flag;
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    save: function(){
		var detail = Ext.getCmp('grid'),records = detail.store.data.items;
		field = Ext.getCmp('dl_tablename'),val = field.value.split(' ')[0];
		Ext.Array.each(records, function(item){
			if(item.data['dld_field'] != null && item.data['dld_field'] != '' && Ext.isEmpty('dld_table')){
				item.set('dld_table', val);
			}
		});
		var me = this;
		if(! me.FormUtil.checkForm()){
			return;
		}
		var de = detail.getChange();
		me.FormUtil.update(Ext.getCmp('form').getValues(), Ext.encode(de.added), 
				Ext.encode(de.updated), Ext.encode(de.deleted));
    },
    copyConfig: function(caller){
    	var me = this;
    	var dl_caller = Ext.getCmp('dl_caller');
    	if(dl_caller&&dl_caller.value){
    		var caller = dl_caller.value;
    		/*Ext.MessageBox.minPromptWidth = 300;
	        Ext.MessageBox.defaultTextHeight = 200;
	        Ext.MessageBox.style= 'background:#e0e0e0;';
	        Ext.MessageBox.prompt("请输入caller", '',
	        function(btn, text) { 
		        if (btn == 'ok') {
	                if (text) {
	                    //record.set(name, text);
	                }else{
	                	showError('caller不能为空！');
	                	return false;
	                }
	            }
	        },
	        this, false,
	        caller);*/
    	//}
    	
	    	Ext.create('Ext.window.Window', {
				title : '请输入caller',
				closeAction: 'destroy',
				modal : true,
				width : 300,
				height: 54,
				layout: {
			        type: 'hbox',
			        pack: 'center',
			        align: 'middle'
	    		},
				items: [{	
					xtype:'textfield',
					value:caller,
					width:270
				}],
				buttonAlign : 'center',
				buttons : [{
					text : $I18N.common.button.erpConfirmButton,
					height : 26,
					handler : function(b) {
						var newcaller = b.ownerCt.ownerCt.down('textfield').value;
						var id = Ext.getCmp('dl_id').value;
						if(id&&newcaller){
							me.FormUtil.setLoading(true);
							Ext.Ajax.request({
								url : basePath + 'ma/cpoyDataList.action',
								params: {
									id:id,
									newCaller:newcaller
								},
								method : 'post',
								callback : function(options,success,response){
									me.FormUtil.setLoading(false);
									var res = new Ext.decode(response.responseText);
									if(res.success){
										showMessage('复制成功',res.msg);
									} else if(res.exceptionInfo){					
										showError(res.exceptionInfo);
									} 
								}
							});
						}else{
							showError('caller不能为空！');
						}
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						var w = b.ownerCt.ownerCt;
						w.close();
					}
				}]
			}).show();
    	}
    }
});