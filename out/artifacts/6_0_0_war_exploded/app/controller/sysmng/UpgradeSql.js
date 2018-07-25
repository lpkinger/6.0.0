/**
 * 升级SQL维护界面Controller
 * */
Ext.QuickTips.init();
Ext.define('erp.controller.sysmng.UpgradeSql', {
    extend: 'Ext.app.Controller',
    requires: [],    
    views: ['sysmng.upgrade.sql.ViewPort','sysmng.upgrade.sql.UpgradSqlFormPanel',
    'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update'],

    init: function() {
    	var me = this;
    	this.control({
    		'upgradSqlForm':{
    			aftersetvalue:function(form){
	    			form.getForm().getFields().each(function (item,index, length){
						var value = item.value == null ? "" : item.value;
	           			item.originalValue=value; 
	    			})
        		}
        	},
	    	'erpAddButton': {
                click: function(btn) {
                    this.onAdd("addUpgradeSql", "升级SQL",'jsps/sysmng/upgradesql.jsp');
                },
                aftersetvalue:function(btn){
                	var value = Ext.getCmp('NUM_').getValue();
                    if (value!=null&&value!='') {
                        btn.show();
                    }
                }
            },
            'erpSaveButton': {
            	aftersetvalue:function(btn){
                	var value = Ext.getCmp('NUM_').getValue();
                    if (value!=null&&value!='') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	var form = btn.ownerCt.ownerCt.ownerCt;
                	me.save(form);
            	}
            },
             'erpUpdateButton': {
             	aftersetvalue:function(btn){
                	var value = Ext.getCmp('NUM_').getValue();
                    if (value!=null&&value!='') {
                        btn.show();
                    }
                },
                click: function(btn) {
                	var form = btn.ownerCt.ownerCt.ownerCt;
					if(!me.checkFormDirty(form)){
						Ext.Msg.alert('警告','未修改数据');
						return;
					}
                	me.update(form);	
                }
            },
            'erpDeleteButton': {
             	aftersetvalue:function(btn){
                	var value = Ext.getCmp('NUM_').getValue();
                    if (value!=null&&value!='') {
                        btn.show();
                    }
                },
                click: function(btn) {
                	var value = Ext.getCmp('NUM_').getValue();
                   	Ext.Ajax.request({
						url : basePath + 'upgrade/deleteUpgradeSql.action',
						params : {
							id:value
						},
						method : 'post',
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.success){
								Ext.Msg.alert('删除成功','删除成功！',function(){
								var main = parent.Ext.getCmp("upgradsqlpanel"); 
								if(main){
									main.getActiveTab().close();
					           	}
								});
							} else if(res.exceptionInfo){
								var str = res.exceptionInfo;
								Ext.Msg.alert('删除失败',str);
							}
			          	}
			      	})
                }
            },
            '#testbtn':{
            	click:function(btn){
            		var value = Ext.getCmp('SQL_').getValue();
            		if(value==null||value==''){
            			Ext.Msg.alert('提示','未输入SQL语句');
            			return;
            		}            		
            		me.testSQL(btn);	
            	},
            	aftertest:function(res){
            		var str ='';
            		if(res.success){
						if(res.result){
							Ext.Msg.alert('测试成功','SQL语句测试OK');
							Ext.getCmp('STATUS_').setValue(1);
						}else{
							Ext.Array.each(res.errorSqls,function(error){
								str+="错误语句："+error.errorSql+'<br>'+'错误信息：'+error.errorInfo+'<br>';
							})
							Ext.Msg.alert('测试错误信息',str);
							Ext.getCmp('STATUS_').setValue(0);
						}								
					} else if(res.exceptionInfo){
						str = res.exceptionInfo;
						Ext.Msg.alert('测试失败',str);
					} 
            	}
            },
            'erpCloseButton': {
                click: function(btn) {
                    var main = parent.Ext.getCmp("upgradsqlpanel"); 
					if(main){
						main.getActiveTab().close();
		           	}
            	}
            }
	    });
    },
 	checkForm: function(form){
		var s = '';
		form.getForm().getFields().each(function (item, index, length){
			if(!item.isValid()){
				if(s != ''){
					s += ',';
				}
				if(item.fieldLabel || item.ownerCt.fieldLabel){
					s += item.fieldLabel || item.ownerCt.fieldLabel;
				}
			}
		});
		if(s == ''){
			return true;
		}
		Ext.Msg.alert('警告',$I18N.common.form.necessaryInfo1 + '(<font color=green>' + s.replace(/&nbsp;/g,'') + 
				'</font>)' + $I18N.common.form.necessaryInfo2);
		return false;
	},
	checkFormDirty: function(form){
		var s = '';
		form.getForm().getFields().each(function (item,index, length){
			var value = item.value == null ? "" : item.value;
			item.originalValue = item.originalValue == null ? "" : item.originalValue;
			if(Ext.typeOf(item.originalValue) != 'object'){	
				if(item.originalValue.toString() != value.toString()){//isDirty、wasDirty、dirty一直都是true，没办法判断，所以直接用item.originalValue,原理是一样的
					var label = item.fieldLabel;
					if(label){
						s = s + '&nbsp;' + label.replace(/&nbsp;/g,'');
					}
				}
			}
		});
		return (s == '') ? false : ('表单字段(<font color=green>'+s+'</font>)已修改');
	},
	save:function(){
		var me = this;
		var form = arguments[0];
		if(!me.checkForm(form)){
			return;
		}
    	if(form.getForm().isValid()){	
			var r = form.getValues();
			var store = unescape(escape(Ext.JSON.encode(r)));
			Ext.Ajax.request({
				url : basePath + 'upgrade/saveUpgradeSql.action',
				params : {
					formStore:store
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						if(res.data){
							form.getForm().setValues(res.data);
							form.fireEvent('aftersetvalue', form);
							Ext.getCmp("addbtn").fireEvent('aftersetvalue', Ext.getCmp("addbtn"));
							Ext.getCmp("save").fireEvent('aftersetvalue', Ext.getCmp("save"));
							Ext.getCmp("updatebutton").fireEvent('aftersetvalue', Ext.getCmp("updatebutton"));
							Ext.getCmp("deletebutton").fireEvent('aftersetvalue', Ext.getCmp("deletebutton"));
							Ext.Msg.alert('保存成功','保存成功！');
						}
					} else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						Ext.Msg.alert('保存错误',str);
					} else{
						saveFailure();//@i18n/i18n.js
					}
	          	}
	      	})
    	}
	},
	update:function(){
		var me = this;
		var form = arguments[0];
		if(!me.checkForm(form)){
			return;
		}
    	if(form.getForm().isValid()){	
			var r = form.getValues();
			var store = unescape(escape(Ext.JSON.encode(r)));
			Ext.Ajax.request({
				url : basePath + 'upgrade/updateUpgradeSql.action',
				params : {
					formStore:store
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						if(res.data){
							form.getForm().setValues(res.data);
							form.fireEvent('aftersetvalue', form);
							Ext.getCmp("addbtn").fireEvent('aftersetvalue', Ext.getCmp("addbtn"));
							Ext.getCmp("save").fireEvent('aftersetvalue', Ext.getCmp("save"));
							Ext.getCmp("updatebutton").fireEvent('aftersetvalue', Ext.getCmp("updatebutton"));
							Ext.getCmp("deletebutton").fireEvent('aftersetvalue', Ext.getCmp("deletebutton"));
							Ext.Msg.alert('更新成功','更新成功！');
						}
					} else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						Ext.Msg.alert('更新错误',str);
					}
	          	}
	      	})
    	}
	},
	testSQL:function(btn){
		var num = Ext.getCmp('NUM_').getValue();
		var value = Ext.getCmp('SQL_').getValue();
		Ext.Ajax.request({
			url : basePath + 'upgrade/checksql.action',
			params : {
				id:num,
				sqls:value
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				btn.fireEvent('aftertest',res,btn);
			}
    	})
    	
	},
	openTab : function (panel,id){ 
    		var o = (typeof panel == "string" ? panel : id || panel.id); 
    		var main = parent.Ext.getCmp("upgradsqlpanel"); 
    		var tab = main.getComponent(o); 
    		if (tab) { 
    			main.setActiveTab(tab); 
    		} else if(typeof panel!="string"){ 
    			panel.id = o; 
    			var p = main.add(panel); 
    			main.setActiveTab(p); 
    		} 
    	} ,
   onAdd: function(panelId, title, url){
    		var main = parent.Ext.getCmp("upgradsqlpanel");
    		if(main){
    			panelId = panelId == null
    			? Math.random() : panelId;
    			var panel = Ext.getCmp(panelId); 
    			if(!panel){ 
    				var value = "";
    				if (title.toString().length>5) {
    					value = title.toString().substring(0,5);	
    				} else {
    					value = title;
    				}
    				if(!contains(url, 'http://', true) && !contains(url, basePath, true)){
    					url = basePath + url;
    				}
    				panel = { 
    						title : value,
    						tag : 'iframe',
    						tabConfig:{tooltip:title},
    						border : false,
    						layout : 'fit',
    						iconCls : 'x-tree-icon-tab-tab',
    						html : '<iframe id="iframe_add_'+panelId+'" src="' + url+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
    						closable : true
    				};
    				this.openTab(panel, panelId);
    			} else { 
    				main.setActiveTab(panel); 
    			}
    		} else {
    			if(!contains(url, basePath, true)){
    				url = basePath + url;
    			}
    			window.open(url);
    		}
    	}
});