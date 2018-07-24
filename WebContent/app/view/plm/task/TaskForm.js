Ext.define('erp.view.plm.task.TaskForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.TaskForm', 
    id: 'uploadform',
    layout: 'column',
    bodyStyle: {background: '#f1f1f1'},
    fieldDefaults: {
						labelAlign : "right",
						columnWidth: .6
					},
	FormUtil: Ext.create('erp.util.FormUtil'),
	listeners: {
        	afterrender: function(){
        		var form = Ext.getCmp('form');
        		form.add({
        			xtype: 'hidden',
        			name: 'files',
        			fieldLabel: '附件',
        			value: '',
        			id: 'files'
        		});
        	}
        },
	/**items: [{
						xtype: 'filefield',
				        fieldLabel: '附件',
				        id: 'attach',
				        name: 'file',
				        msgTarget: 'side',
				        allowBlank: false,
				        buttonText: '浏览...',
				        listeners: {
				        	afterrender: function(field){
				        		field.attachcount = 0;
				        		upload.files = new Array();
				        	},
				        	change: function(field){
				        		if(field.value != null){
				        			field.button.disable(true);
		        					var container = Ext.create('Ext.form.FieldContainer', {
		        						layout: 'hbox',
		        						fieldLabel: "附件" + (field.attachcount + 1),
		        				        items: [{
		        				            xtype: 'textfield',
		        				            id: 'attach' + field.attachcount,
		        				            flex: 1
		        				        }, {
		        				            xtype: 'button',
		        				            text: '上传',
		        				            id: 'upload' + field.attachcount,
		        				            handler: function(btn){
		        				            	var form = btn.ownerCt.ownerCt;
		        				            	var f = Ext.getCmp(btn.id.replace('upload', 'attach'));
		        				            	if(f.value != null && f.value != ''){
		        				            		//field.value = f.value;
		        				            		form.getForm().submit({
		            				            		url: basePath + 'common/upload.action?em_code=' + em_code,
		            				            		waitMsg: "正在上传:" + f.value,
		            				            		success: function(fp, o){
		            				            			if(o.result.error){
		            				            				showError(o.result.error);
		            				            			} else {
		            				            				Ext.Msg.alert("恭喜", f.value + " 上传成功!");
		                				            			btn.setText("上传成功(" + Ext.util.Format.fileSize(o.result.size) + ")");
		                				            			btn.disable(true);
		                				            			field.button.setDisabled(false);
		                				            			Ext.getCmp('files').setValue(Ext.getCmp('files').value + ',' + o.result.filepath);
		                				            			upload.files[Number(btn.id.replace('upload', ''))] = o.result.filepath;
		            				            			}
		            				            		}
		            				            	});
		        				            	}
		        				            },
		        				            flex: 1
		        				        }, {
		        				            xtype: 'button',
		        				            text: '删除',
		        				            id: 'delete' + field.attachcount,
		        				            handler: function(btn){
		        				            	var f = Ext.getCmp(btn.id.replace('delete', 'attach'));
		        				            	if(f.value != null && f.value != ''){
		        				            		Ext.getCmp('files').setValue(Ext.getCmp('files').value.replace(upload.files[Number(btn.id.replace('delete', ''))], ''));
		        				            		upload.files[Number(btn.id.replace('delete', ''))] = '';
		        				            	}
		        				            	btn.ownerCt.destroy(true);
		        				            	field.attachcount--;
		        				            },
		        				            flex: 1
		        				        }]
		        					});
		        					if(contains(field.value, "\\", true)){
		        						Ext.getCmp('attach' + field.attachcount).setValue(field.value.substring(field.value.lastIndexOf('\\') + 1));
		        					} else {
		        						Ext.getCmp('attach' + field.attachcount).setValue(field.value.substring(field.value.lastIndexOf('/') + 1));
		        					}
		        					Ext.getCmp('uploadform').insert(1, container);
		        					field.attachcount++;
		        					field.button.setText("继续...");
				        		}
				        	}
				        }
				    },{
				        xtype: 'displayfield',
				        id: 'attachs',
				        cls: 'mail-attach',
				        height: 'auto'
				    }],**/
	initComponent : function(){ 
		this.callParent(arguments);
	},
	listeners: {
		collapse: function(){			
		},
		expand: function(){			
		}
	}
});

