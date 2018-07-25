Ext.define('erp.view.core.form.PhotoField', {
	extend: 'Ext.form.FieldContainer',
    alias: 'widget.photofield',
    layout: 'column',
    defaults:{
    	hideLabel: true
    },
    initComponent : function(){
    	this.title = this.fieldLabel;
    	this.fieldLabel = ' ';//不需要label
    	this.labelSeparator = '';
    	var readOnly = this.readOnly;
    	//logic类型为 edit 时一直为可编辑  主要是为了老客户添加PF类型时可能想手动添加图片 单据却是已审核的情况
    	if(this.logic=='edit'){
    		readOnly = false
    	}
    	this.callParent(arguments);
    	var basefield=this;
    	var me = this;
    	me.insert(0,{
		    xtype: 'hidden',
		    value: '',
		    name :me.name,
		    columnWidth:0
		});
    	me.insert(1,{
    	xtype:'form',
    	layout:'fit',
    	id:'baseform',
    	height:170,
    	width:135,
    	bodyStyle: 'background: transparent no-repeat 0 0;border: none;',	
		items: [{
			padding:'0 0 1 0',
			readOnly:true,
			xtype: 'htmleditor',
			cls:'x-pf-htmleditor',
			name:'msg',
			fieldSubTpl: [
	        	'<textarea id="{cmpId}-textareaEl" name="{name}" tabIndex="-1" class="{textareaCls}" ',
	            'style="{size}" autocomplete="off" readOnly="true"></textarea>',
	        	'<iframe id="{cmpId}-iframeEl" readOnly="true" name="{iframeName}" frameBorder="0" style="padding:2px 5px 0px 5px;" src="{iframeSrc}"></iframe>',
	        	'<div id="{cmpId}-toolbarWrap" class="{toolbarWrapCls}"></div>',
	            {
	            	compiled: true,
	            	disableFormats: true
	            }
            ],
			createToolbar : function(editor){
			 	var me = this,
            	items = [],
                tipsEnabled = Ext.tip.QuickTipManager && Ext.tip.QuickTipManager.isEnabled(),
                baseCSSPrefix = Ext.baseCSSPrefix,
                fontSelectItem, toolbar, undef;
				toolbar = Ext.widget('toolbar', {
					cls:'x-pf-toolbar',
					id:'x-pf-toolbar',
					height:36,
	                renderTo: me.toolbarWrap,
	                dock: 'bottom',
	                enableOverflow: false,
	                items:[readOnly?'->':'',{
	                   id:'x-pf-look',
	                   margin:readOnly?'':'0 0 0 10',
	                   text:'查看',
	                   noControl:true, //noControl属性禁用编辑框不禁用toolbar
	                   xtype:'button',
	                   cls:'x-btn-gray',
	                   handler:function(){
	                   	    var form = Ext.getCmp('baseform');
	                   	    var val = form.ownerCt.value;
	                   	    if(val==''){
	                   	    	showMessage('未上传图片或图片内容有误，无法查看')
	                   	    	return
	                   	    }
	                  	    var me = this, resizer = me.resizer,
							imageframe = document.getElementById('ext-image-frame');
							src = basePath + 'common/download.action?path=' + val.replace(/\+/g, '%2B');
							if (!imageframe) {
								var el = Ext.DomHelper.append(document.body, '<img id="ext-image-frame" src="' + src +
										'" width="500" height="400" style="position:absolute;left:0;top:0;"/>', true);
								imageframe = el.dom;
							} else {
								imageframe.src = src;
							}
							if (!resizer) {
								resizer = this.resizer = Ext.create('Ext.resizer.Resizer', {
									target: 'ext-image-frame',
									pinned: true,
									width: 510,
									height: 410,
									minWidth: 100,
									minHeight: 80,
									preserveRatio: true,
									handles: 'all',
									dynamic: true
								});
								var resizerEl = resizer.getEl();
								resizerEl.on('dblclick', function(){
									resizerEl.hide(true);
								});
							}
							resizer.getEl().center();
							resizer.getEl().show(true);
							Ext.DomHelper.applyStyles(imageframe, 'position:absolute;z-index:100;');
	                  } 
	               },readOnly?'->':'',{
	               	   noControl:true,
	               	   hidden:readOnly,
	                   id:'fileform',
	                   xtype:'form',
	    	           layout:'column',
	    	           bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
				       items: [{
							xtype: 'filefield',
							name: 'file',
							buttonOnly: true,
					        hideLabel: true,
					        height: 26,
							buttonConfig: {
								cls:'x-btn-gray',
								text: '编辑',
								id:'photobutton'
					        },
					        createFileInput : function() {
				               var a = this;
				                a.fileInputEl = a.button.el.createChild({
						           name : a.getName(),
						           cls : Ext.baseCSSPrefix + "form-file-input",
						           tag : "input",
						           type : "file",
						           size : 1,
						           accept:"image/*"
					           }).on("change", a.onFileChange, a)
				            },
					        listeners: {
								change: function(field){
									field.ownerCt.getForm().submit({
					            		url: basePath + 'common/uploadPic.action?em_code=' + em_code+'&caller='+caller,
					            		waitMsg: "正在解析图片信息",
					            		success: function(fp, o){
					            			if(o.result.error){
					            				showError(o.result.error);
					            			} else {
					            				var msg = Ext.getCmp('baseform').ownerCt.ownerCt.down('htmleditor[name=msg]');
					            				var imgel=msg.getEl().dom.getElementsByTagName('iframe')[0].contentWindow.document.body;
					            				if(imgel.getElementsByTagName('img').length>0){
					            				  imgel.removeChild(imgel.getElementsByTagName('img')[0]);
					            				}
					            				//Ext.getCmp('baseform').ownerCt.ownerCt.down('hidden').setValue('../../../'+o.result.path);
					            				Ext.getCmp('baseform').ownerCt.setValue(o.result.path);
					            		        var element = document.createElement("img");
					            		        element.src = basePath + 'common/download.action?path=' + o.result.path.replace(/\+/g, '%2B');
					            		        element.style="width:100%;height:100%";            
					            		        element.title = '&img' + o.result.filepath + ";";
					            		        msg.setValue('<img src="'+element.src+'" style=width:100%;height:100%;>');
					            			}
					            		}
					            	});
								}
							}
						}]
	                }]
               });
               me.toolbar = toolbar;
		    }									
			}]
    	});   	
    },
	listeners : {
		afterrender: function(f){
			if(f.groupName!=''){
				Ext.getCmp('x-pf-toolbar').el.dom.style.height = '32px';
			}
			this.getEl().dom.childNodes[1].style.overflow = 'hidden';
			var form = f.ownerCt;
			if(f.value){
				var src = basePath + 'common/download.action?path=' + f.value.replace(/\+/g, '%2B');
				var msg = Ext.getCmp('baseform').ownerCt.ownerCt.down('htmleditor[name=msg]').setValue('<img src="'+src+'" style=width:100%;height:100%;>');			
			}else{
				var msg = Ext.getCmp('baseform').ownerCt.ownerCt.down('htmleditor[name=msg]').setValue('<img src="'+basePath+'/resource/images/upgrade/bluegray/mainicon/header.png'+'" style=width:100%;height:100%;>');
			}
		}
	},
	setValue: function(value){
    	this.items.items[0].setValue(value);
    },
    setReadOnly:function(){
    	
    },
    setFieldStyle:function(){
    	
    }
});