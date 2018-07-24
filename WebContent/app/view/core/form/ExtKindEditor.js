/**
 * html 编辑框
 * @author chenrh
 */
Ext.define('erp.view.core.form.ExtKindEditor', {
	extend : 'Ext.form.field.TextArea',
	alias : 'widget.extkindeditor',//xtype名称
	height: 600,
	initComponent : function() {
		var me = this;
		this.bodyEl = "<textarea id='" + this.getId() + "-input' name='" + this.name + "'></textarea>";
		KindEditor.plugin('preview', function(K) {
			var self = this, name = 'preview';
			self.clickToolbar(name, function() {
				myWindow = window.open(basePath + 'jsps/wisdomPark/preview.jsp?id=' + me.getId());
				myWindow.focus();
			});
		});

		this.callParent(arguments);
		
		this.on("afterrender", function(t) {
			t.up('form').on("afterlayout", function(form) {
				if(!t.editor){
					t.editor = KindEditor.create('textarea[#"' + this.getId() + '-input"]', {
						basePath : basePath + 'resource/KindEditor/',
						uploadJson : basePath + 'wisdomPark/file/upload.action',//上传
						fileManagerJson : basePath + 'wisdomPark/file/fileManager.action',//文件管理
						resizeType : 0,
						wellFormatMode : true,
						newlineTag : 'br',
						allowFileManager : true,
						allowPreviewEmoticons : true,
						allowImageUpload : true,
						items : [
								'source', '|', 'undo', 'redo', '|', 'justifyleft', 'justifycenter', 'justifyright',
						        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
						        'superscript', 'code', 'clearhtml', 'quickformat', 'selectall','anchor', 'link', 'unlink', '|', 'fullscreen', 'preview', '/',
						        'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
						        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
						        'flash', 'media', 'insertfile', 'table', 'hr', 'emoticons', 'baidumap', 'pagebreak','wordpaste'
						],
						afterCreate : function() {
							var content = t.getEl();
							var input = content.down('iframe').dom.contentDocument.getElementsByTagName('body')[0];
		    				t.focusEl = t.inputEl = Ext.get(input);
		    				t.inputId = t.inputEl.id;
		    				t.initEvents();
					}
					});
					
					t.on("resize", function(t, w, h) {
						t.editor.resize(w - t.labelWidth-5, h);
					});
					var v = t.value;
    				t.setValue(v);
    				v.originalValue = v;
				}
			});
		});
	},
	setValue : function(value) {
		if (this.editor) {
			this.editor.html(value);
		}
	},
	reset : function() {
		if (this.editor) {
			this.editor.html('');
		}
	},
	setRawValue : function(value) {
		if (this.editor) {
			this.editor.text(value);
		}
	},
	getValue : function() {
		if (this.editor) {
			return this.editor.html();
		} else {
			return ''
		}
	},
	getRawValue : function() {

		if (this.editor) {
			return this.editor.text();
		} else {
			return ''
		}
	}
});
