/**
 * 显示附件名称、大小;
 * 附件删除、下载、显示
 */
Ext.define('erp.view.core.trigger.TrashField2', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.trashfield2',
	trigger1Cls: 'x-form-download-trigger',
	trigger2Cls: 'x-form-trash-trigger',
	fieldStyle: 'background:#C6E2FF;color:#D9D9D9;',
	autoShow: true,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners : {
		afterrender : function() {
			var me = this, c = Ext.Function.bind(me.onTrigger1Click, me);
			Ext.EventManager.on(me.inputEl, {
				click : c,
				scope : me,
				buffer : 100
			});
			Ext.DomHelper.applyStyles(me.inputEl.dom, 'border:none;');
		}
	},
	onTrigger1Click: function(e){
		var path = this.realpath;
		var fileName = this.fileName;
		if(this.isImage(fileName)) {
			var menu = this.createMenu();
			menu.path = path; 
			menu.showAt(e.getXY());
		} else {
			this.download();
		}
	},
	onTrigger2Click: function(){
		var me = this;
		warnMsg('确定删除' + me.value, function(btn){
			if(btn == 'yes'){
				var v = me.ownerCt.down('hidden').value.replace(me.filepath + ';', '');
				me.ownerCt.down('hidden').setValue(v);
				me.ownerCt.filesize -= me.filesize;
				me.ownerCt.setTitle('<img src="' + basePath + 'resource/images/icon/clip.png" width=20 height=20/>附件' + 
						'(总大小:' + Ext.util.Format.fileSize(me.ownerCt.filesize) + ")");
				me.destroy();
			}
		});
	},
	download : function() {
		var me = this;
		if(me.ownerCt.ownerCt.xtype=='erpFormPanel2'){
			id='ext-window';
		}
		if (!Ext.fly('ext-attach-download')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-attach-download';  
			frm.name = id;  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);
		}
		Ext.Ajax.request({
			url: basePath + 'common/downloadbyId.action',
			method: 'post',
			form: Ext.fly('ext-attach-download'),
			isUpload: true,
			params: {
				id: me.filepath
			}
		});
	},
	createMenu : function() {
		var me = this, menu = this.menu;
		if (!menu) {
			menu = this.menu = Ext.create('Ext.menu.Menu', {
				items : [{
					text : '查看',
					handler : function() {
						me.showAttach(menu.path);
					}
				}, {
					text : '下载',
					handler : function() {
						me.download();
					}
				}]
			});
		}
		return menu;
	},
	showAttach : function() {
		var me = this,src = basePath + 'common/download.action?path=' + me.realpath.replace(/\+/g, '%2B')+'&fileName='+me.fileName+'&size='+me.filesize;
		var img = document.createElement("img");
		img.src = src;		 		
		myWindow=window.open(); 
		myWindow.document.body.appendChild(img);
		myWindow.focus();
	},
	isImage : function(path) {
		if (Ext.isEmpty(path)) {
			return false;
		}
		if (!/\.(gif|jpg|jpeg|png|GIF|JPG|PNG)$/.test(path)) {
			return false;
		}
		return true;
	},
	resetField:function(bool){		
	    var me=this;
		if(bool)
		me.getEl().down("." + me.trigger2Cls).applyStyles({visibility: 'hidden'});
		else{
			 me.getEl().down("." + me.trigger2Cls).applyStyles({visibility: 'visible'});
		}
	}
});
