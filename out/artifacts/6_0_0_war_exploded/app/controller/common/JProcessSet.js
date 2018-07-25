Ext.QuickTips.init();
Ext.define('erp.controller.common.JProcessSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','common.JProcess.JProcessSet','core.grid.Panel','core.button.Add','core.button.Submit','core.button.Audit',
    		'core.button.Save','core.button.Close','core.button.Print','core.button.Upload','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger', 'core.button.Sync','common.JProcess.JProcessSetLock'
    	],
    	 init:function(){
    	    	var me = this;
    	    	/*formCondition = this.BaseUtil.getUrlParam('formCondition');
    	    	console.log(formCondition);*/
    	    	var js_formurl=Ext.decode(Ext.getCmp('js_formurl'));
    	    	this.control({   
    	    	'#form': {
    	    		afterload: function(form) {
    	    			// 2018050469  单据关闭二次确认逻辑  zhuth  界面加载出来后重设所有字段dirty状态
						Ext.defer(function(){
							var fields = form.getForm().getFields().items;
							Ext.Array.each(fields, function(f) {
								f.resetOriginalValue ? f.resetOriginalValue() : '';
							});
						}, 800);
    	    		}
    	    	},
    	 		'erpSaveButton': {    			
    	 			afterrender: function(btn){
    					var forcon = getUrlParam('formCondition');

    					if(forcon != null && forcon != ''){
    							btn.hide();
    						}
    				},
    	    			click: function(btn){
    	    				me.FormUtil.beforeSave(me);
    	    			}
    	    		},
    	    		'erpDeleteButton' : {
    	    			
    	    			afterrender: function(btn){
    	    				var forcon = getUrlParam('formCondition');
    	    				if(forcon == null || forcon == ""){
    	    						btn.hide();
    	    					}
    	    			},
    	    			click: function(btn){
    	    				if(Ext.getCmp('js_caller').value==null||Ext.getCmp('js_caller').value==''){
    	    					btn.hide();
    	    				}
    	    				me.FormUtil.onDelete(Ext.getCmp('js_id').value);
    	    			}
    	    		},
    	    		'erpPostButton' : {
    	    			
    	    			afterrender: function(btn){
    	    				var forcon = getUrlParam('formCondition');
    	    				if(forcon == null || forcon == ""){
    	    						btn.hide();
    	    					}
    	    			},
    	    			click:function(btn){
    	    				   				
    	    				me.FormUtil.onPost(Ext.getCmp('js_id').value);
    	    			}
    	    		},
    	    		'erpUpdateButton': {
    	    			
    	    			afterrender: function(btn){
    	    				var forcon = getUrlParam('formCondition');
    	    				if(forcon == null || forcon == ""){
    	    						btn.hide();
    	    					}
    	    			},
    	    			click: function(btn){	
    	    				me.beforeUpdate();
    	    			}
    	    		},
    	    		'erpAddButton': {
    	    			
    	    			afterrender: function(btn){
    	    				var forcon = getUrlParam('formCondition');
    	    				if(forcon == null || forcon == ""){
    	    						btn.hide();
    	    					}
    	    			},
    	    			click: function(){
    	    				
    	    				me.FormUtil.onAdd('jprocessSet', '新增流程设置', 'jsps/common/jprocessSet.jsp?whoami='+caller);
    	    			}
    	    		},
    	    		'erpCloseButton': {
    	    			afterrender: function(btn){//特殊处理按钮组
    	    				var form = Ext.getCmp('form');
    	    				if(form){
    	    					me.autoSetBtnStyle(form);
    	    				}
    	    			},
    	    			click: function(btn){
    	    				me.FormUtil.beforeClose(me);    				
    	    			}
    	    		}
    	    		
    	    });},
    	    getForm: function(btn){
    			return btn.ownerCt.ownerCt;
    		},
    		beforeUpdate: function(){
    			var bool = true;
    			if(bool)
    				this.FormUtil.onUpdate(this);
    		},
    		autoSetBtnStyle : function(from) {
				var t = Ext.getCmp('form_toolbar');
				Ext.each(t.items.items,function(group,index){
					if(group.items){
						var _first = _last = -1;
						Ext.each(group.items.items,function(item,index){
							if(!item.hidden){						
								if(_first==-1){_first = index;}
								_last = index;
							}
						});
						if(_first>-1){
							if(group.items.items[_first].el&&group.items.items[_last].el){
								group.items.items[_first].el.dom.classList.add('x-group-btn-first');
								group.items.items[_last].el.dom.classList.add('x-group-btn-last');
							}
						}
					}
				});
			}
    	});