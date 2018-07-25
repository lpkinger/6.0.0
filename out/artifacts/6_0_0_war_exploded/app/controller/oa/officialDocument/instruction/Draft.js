Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.instruction.Draft', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.officialDocument.instruction.Draft','core.form.Panel','core.form.WordSizeField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.File','core.button.Transmit',
    		'core.button.Update','core.button.Delete','core.button.Submit','core.button.Over','core.form.FileField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	me.attachcount = 0;
    	me.files = new Array();
    	me.attach = '';
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var mm = this.FormUtil;
    				var form = Ext.getCmp('form');
    				if(! mm.checkForm()){
    					return;
    				}
    				if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
    					mm.getSeqId(form);
    				}
    				console.log(form);
    				if(form.getForm().isValid()){
    					//form里面数据
    					Ext.each(form.items.items, function(item){
    						if(item.xtype == 'numberfield'){
    							//number类型赋默认值，不然sql无法执行
    							if(item.value == null || item.value == ''){
    								item.setValue(0);
    							}
    						}
    					});
    					var r = form.getValues();
    					var keys = Ext.Object.getKeys(r);
    					var values = Ext.Object.getValues(r);
    					var o = new Object();
    					Ext.each(keys, function(key, index){
    						if(!contains(key, 'ext-', true)){
    							o[key] = values[index];
    						}
    					});
    					o.in_attach = '';
    					Ext.each(me.files, function(){
    						o.in_attach += this + ",";
    					});
//    					console.log(o);
    					o.in_attach = o.in_attach.substring(0, o.in_attach.lastIndexOf(','));
    					if(!mm.contains(form.saveUrl, '?caller=', true)){
    						form.saveUrl = form.saveUrl + "?caller=" + caller;
    					}
//    					console.log(o);
    					mm.save(o, []);
    				}else{
    					mm.checkForm();
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.onClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('in_id').value);
    				alert('提交成功');
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			}
    		},
    		'erpOverButton': {
    			click: function(btn){
    				Ext.getCmp('in_status').setValue('已结束');
    				Ext.getCmp('in_statuscode').setValue('OVERED');
    				this.FormUtil.onUpdate(this);
    				alert('提交成功');
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			}
    		},
//    		'erpAddButton': {
//    			click: function(){
//    				me.FormUtil.onAdd('addDraft', '新增发文拟稿', 'jsps/oa/officialDocument/sendODManagement/draft.jsp');
//    			}
//    		},
    		'textfield[id=in_dept]': {
    			render: function(field){
    				Ext.Ajax.request({//拿到grid的columns
    		        	url : basePath + "hr/employee/getHrOrg.action",
    		        	params: {
    		        		em_id: em_uu
    		        	},
    		        	method : 'post',
    		        	async: false,
    		        	callback : function(options, success, response){
    		        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
    		        		console.log(response);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		if(res.hrOrg){
    		        			field.setValue(res.hrOrg.or_name);
    		        			field.setReadOnly(true);
    		        		}
    		        	}
    				});
    			}
    		},
    		'htmleditor[id=in_context]': {
    			afterrender: function(f){
    				f.setHeight(400);
    			}
    		},
    		'filefield[id=file]': {
    			change: function(field){
    				var mm = this.FormUtil;
    				var fo = Ext.getCmp('form');
    				if(field.value != null){
    					var container = Ext.create('Ext.form.FieldContainer', {
    						layout: 'hbox',
    						fieldLabel: "附件" + (me.attachcount + 1),
    						columnWidth: 1,
    				        items: [{
    				            xtype: 'textfield',
    				            id: 'attach' + me.attachcount,
    				            flex: 1
    				        },{
    				            xtype: 'button',
    				            text: '上传',
    				            id: 'upload' + me.attachcount,
    				            handler: function(btn){
    				            	var form = btn.ownerCt.ownerCt;
    				            	var f = Ext.getCmp(btn.id.replace('upload', 'attach'));
    				            	if(Ext.getCmp(fo.keyField).value == null || Ext.getCmp(fo.keyField).value == ''){
    			    					mm.getSeqId(fo);
    			    				}
    				            	if(f.value != null && f.value != ''){
    				            		//field.value = f.value;
    				            		if(form.getForm().isValid()){
    				            			form.getForm().submit({
            				            		url: basePath + 'oa/officialDocument/upload.action?em_code=' + em_code + 
            				            		'&number=QS_' + Ext.getCmp('in_id').getValue(),
            				            		waitMsg: "正在上传:" + f.value,
            				            		success: function(fp, o){
            				            			console.log(o);
            				            			if(o.result.error){
            				            				showError(o.result.error);
            				            			} else {
            				            				Ext.Msg.alert("恭喜", f.value + " 上传成功!");
                				            			btn.setText("上传成功(" + Ext.util.Format.fileSize(o.result.size) + ")");
                				            			btn.disable(true);
                				            			me.files[Number(btn.id.replace('upload', ''))] = o.result.filepath;
            				            			}
            				            		}
            				            	});
    				            		} else {
    				            			me.FormUtil.checkForm();
    				            		}
    				            	}
    				            },
    				            flex: 1
    				        }, {
    				            xtype: 'button',
    				            text: '删除',
    				            id: 'delete' + me.attachcount,
    				            handler: function(btn){
    				            	var f = Ext.getCmp(btn.id.replace('delete', 'attach'));
    				            	if(f.value != null && f.value != ''){
    				            		me.files[Number(btn.id.replace('delete', ''))] = '';
    				            	}
    				            	btn.ownerCt.destroy(true);
    				            	me.attachcount--;
    				            },
    				            flex: 1
    				        }]
    					});
    					if(me.FormUtil.contains(field.value, "\\", true)){
    						Ext.getCmp('attach' + me.attachcount).setValue(field.value.substring(field.value.lastIndexOf('\\') + 1));
    					} else {
    						Ext.getCmp('attach' + me.attachcount).setValue(field.value.substring(field.value.lastIndexOf('/') + 1));
    					}
    					Ext.getCmp('form').insert(9, container);
    					me.attachcount++;
    					field.button.setText("继续...");
    				}
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});