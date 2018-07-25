/**
 * 显示附件名称、大小;
 * 附件删除、下载、显示
 */
Ext.define('erp.view.core.trigger.FTPField', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.ftpfield',
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
			//取消延时影藏trigger的写法 
		/*	// 只读时，隐藏删除trigger
			Ext.defer(function(){
				var form = me.up('form');
				if(me.ownerCt.readOnly) {
					me.getEl().down("." + me.trigger2Cls).applyStyles({visibility: 'hidden'});
				}
			}, 200);*/
		}
	},
	onTrigger1Click: function(e){
		var path = this.realpath;
		if(this.isImage(path)) {
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
				me.updateAttachField(v, '删除附件', function(){
					me.ownerCt.down('hidden').setValue(v);
					me.ownerCt.filesize -= me.filesize;
					me.ownerCt.setTitle('<img src="' + basePath + 'resource/images/icon/clip.png" width=20 height=20/>附件' + 
							'(总大小:' + Ext.util.Format.fileSize(me.ownerCt.filesize) + ")");
				});
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
		var ftpid = Ext.getCmp('sf_name').value;
		Ext.Ajax.request({
			url: basePath + 'common/downloadbyFTP.action',
			method: 'post',
			form: Ext.fly('ext-attach-download'),
			isUpload: true,
			params: {
				id: me.filepath,
				ftpid: ftpid,
				fileN: this.fileName
			}
		});
	},
	createMenu : function() {
		var me = this, menu = this.menu;
		if (!menu) {
			menu = this.menu = Ext.create('Ext.menu.Menu', {
				items : [{
					text : '下载',
					handler : function() {
						me.download();
					}
				}]
			});
		}
		return menu;
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
	},
	updateAttachField: function(value, type, fn) {
		var form = this.ownerCt.ownerCt;
		if(form.keyField){
			var field = this.ownerCt.name,me = this, val = Ext.getCmp(form.keyField).value;
			if(!Ext.isEmpty(val)&&!(caller.indexOf("$")!=-1 && form.keyField=="cl_id")) {//通用变更单上的附件字段，删除附件时后台不删除
				Ext.Ajax.request({
					url: basePath + 'common/attach/update.action',
					params: {
						caller: caller,
						table: form.tablename.toUpperCase().split("LEFT")[0],
						update: field + '=\'' + value + '\'',
						condition: form.keyField + "='" + val+"'",
						type: type
					},
					callback: function(opt, s, r) {
						var res = Ext.decode(r.responseText);
						if(res.success) {
							if(fn) {
								fn.call();
								me.destroy();
							}
						} else {
							showError(res.exceptionInfo);
						}
					}
				});
			} else {
				fn && fn.call();
				this.destroy();
			}
		} else{
				fn && fn.call();
				this.destroy();
		}
	}
});
