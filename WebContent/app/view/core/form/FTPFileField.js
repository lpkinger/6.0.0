/**
 * 多文件上传下载
 */
Ext.define('erp.view.core.form.FTPFileField', {
	extend: 'Ext.form.FieldSet',
	alias: 'widget.ftpfilefield',
	minHeight: 22,
	collapsible: true,
//	title: '<img src="' + basePath + 'resource/images/icon/clip.png" width=20 height=20/>附件',
	style: 'background:#f1f1f1;',
//	margin: '2 2 2 2',
	filesize: 0,//附件总大小
	multi: true,
	allowBlank:true,
	initComponent: function() {
		this.columnWidth = 1;//强制占一行
		this.cls = '';
		this.title = this.title || '附件';
		this._title = this.title;
		this.callParent(arguments);
		this.items.items[0].name = this.name;
		this.items.items[0].fieldLabel =  this.fieldLabel ;
		this.items.items[0].allowBlank = this.allowBlank;
	},
	layout:'column',
	items: [{
		xtype: 'hidden',//该隐藏字段的值(附件在FilePath表的ID,用;隔开)将被保存到数据库
		value: '',
		fieldLabel: '附件',
		allowBlank:true,
		isValid:function(){
			if(this.allowBlank||this.value){
				return true;
			}
		}
	},{
		xtype: 'form',
		columnWidth: 1,
		frame: false,
		border: false,
		minHeight: 22,
		bodyStyle: 'background:#f1f1f1;padding:2px;',
		layout: 'hbox',
		items: [{
				fieldLabel:"FTP名称",
				id:"sf_name",
				labelAlign:"left",
				dataIndex:"sf_name",
				displayField: 'display',
				valueField: 'value',
				store: Ext.create('Ext.data.Store', {
					autoLoad: true,
				    fields: ['display','value'],
				    proxy: {
			             type: 'ajax',
					     url : basePath + 'common/getFTPData.action',				           
					     reader: {
					          type: 'json',
					          root: 'data'
					     },
					     headers: {
			                 'Content-Type': 'application/json;charset=utf-8'
			             }	
				    }
				}),
				xtype:"combo",
				listeners:{
			       select:function(combo,records,options){
			           var sfd_path=Ext.getCmp('sfd_path');
			           sfd_path.clearValue();
			           sfd_path.store.proxy.extraParams.id = this.value;
			           sfd_path.store.load();
				   }
				}
			},{
				fieldLabel:"FTP保存路径",
				id:"sfd_path",
				labelAlign:"left",
				displayField: 'display',
				valueField: 'value',
				queryMode: 'remote',
				dataIndex:"sfd_path",
				editable : false,
				defaultListConfig:{
                    loadMask: false
               },
				store: Ext.create('Ext.data.Store', {
					    fields: ['display','value'],
					    proxy: {
				             type: 'ajax',
						     url : basePath + 'common/getFTPDetailData.action',				           
						     reader: {
						          type: 'json',
						          root: 'data'
						     },
						     extraParams:{id:0},
						     headers: {
				                 'Content-Type': 'application/json;charset=utf-8'
				             }		                   
				           }
				}),
				
				xtype:"combo"
			},{
			xtype: 'filefield',
			name: 'file',
			buttonText: '<font color=blue>浏览文件...</font>',
			buttonOnly: true,
			hideLabel: true,
			listeners: {
				change: function(field){
					if(field.value != null){
						field.ownerCt.ownerCt.upload(field.ownerCt, field);
					}
				}
			}
		}]
	}],
	setValue: function(value){
		this.value = value;
		if(Ext.isEmpty(value))
			this.down('hidden').setValue('');
		else if(this.items){
			this.clearAll();//不清除会重复添加item
			this.download(value,this.id);
		}
	},
	listeners : {
		afterrender: function(f){
			var form = f.ownerCt;			
			if(f.value != null && f.value.toString().trim() != ''){
				f.download(f.value,f.name);
			}
			if(form.keyField){
				var keyField = form.down('field[name=' + form.keyField + ']');
				if(keyField) {
					keyField.on('change', function(f, nv, ov){
						if(ov){
							Ext.each(f.items.items, function(item, index){
								if(index > 1){
									item.destroy();
								}
							});
							if(f.value != null && f.value.toString().trim() != ''){
								f.download(f.value,f.name);
							}
						}
					});
				}
			}
			f.setReadOnly(f.readOnly);
		}
	},
	/**
	 * 上传附件
	 */
	upload: function(form, field){
		var me = this;
		var filename = '';
		if(contains(field.value, "\\", true)){
			filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
		} else {
			filename = field.value.substring(field.value.lastIndexOf('/') + 1);
		}
		if(me.checkFile(filename)){
			showError('当前类型文件不允许上传!');
			return false;
		}
		var sf_id = Ext.getCmp('sf_name').value;
		var sfd_id = Ext.getCmp('sfd_path').value;
		if(sf_id==''||sf_id==null||sf_id==0){
			showError("请填写FTP名称!");
			return;
		}
		form.getForm().submit({
			url: basePath + 'common/uploadFTP.action?em_code=' + em_code+'&sf_id='+sf_id+'&sfd_id='+sfd_id,
			waitMsg: "正在上传:" + filename,
			success: function(fp, o){
				if(o.result.error){
					showError(o.result.error);
				} else {
					Ext.Msg.alert("恭喜", filename + " 上传成功!");
					me.filesize += o.result.size;
					me.setTitle('<img src="' + basePath + 'resource/images/icon/clip.png" width=20 height=20/>' + me._title +  
							'(总大小:' + Ext.util.Format.fileSize(me.filesize) + ")");
					var name = filename + "  (" + Ext.util.Format.fileSize(o.result.size) + ")";
					var w = Math.min((me.getStrLength(name) + 10) / 200, .8);
					var field = Ext.create('erp.view.core.trigger.FTPField', {
						fileName: filename,
						value: name,
						columnWidth: w,
						readOnly: false,
						editable: false,
						filepath: o.result.filepath,
						filesize: o.result.size,
						realpath: o.result.path,
						fieldStyle: 'background:#E0EEEE;'
					});
					if(!me.multi)
						me.clearAll();
					me.add(field);
					var val = me.down('hidden').value + o.result.filepath + ';';
					me.down('hidden').setValue(val);
					// 单据已经存在的情况下，直接修改attach字段
					var form = me.ownerCt, kf;
					if(form && form.keyField && (kf = form.down('field[name=' + form.keyField + ']')) != null
							&& kf.getValue() != null && kf.getValue() != 0) {
						field.updateAttachField(val, '添加附件');
					}
				}
			}
		});
	},
	/**
	 * 根据id读取对应PATH
	 * @param id{String} fp_id
	 */
	download: function(id,name){
		var me = this;
		var files = new Array();	
		Ext.Ajax.request({
			url : basePath + 'common/getFilePaths.action',
			async: false,
			params: {
				id:  id,
				field:name
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exception || res.exceptionInfo){
					showError(res.exceptionInfo);
					return;
				}
				files = res.files != null ? res.files : [];
			}
		});		
		Ext.each(files, function(f){
			var path = f.fp_path;
			var fileName = f.fp_name;
			if(!fileName) {
				if(contains(path, '\\', true)){
					fileName = path.substring(path.lastIndexOf('\\') + 1);
				} else {
					fileName = path.substring(path.lastIndexOf('/') + 1);
				}
			}
			me.filesize += f.fp_size;
			me.setTitle('<img src="' + basePath + 'resource/images/icon/clip.png" width=20 height=20/>' + me._title +  
					'(总大小:' + Ext.util.Format.fileSize(me.filesize) + ")");
			var name = fileName + "  (" + Ext.util.Format.fileSize(f.fp_size) + ")";
			var w = Math.min((me.getStrLength(name) + 10) / 200, .8);
			me.addItem(Ext.create('erp.view.core.trigger.FTPField', {
				fileName: fileName,
				value: name,
				columnWidth: w,
				readOnly: false,
				editable: false,
				filepath: f.fp_id,
				filesize: f.fp_size,
				realpath: path,
				fieldStyle: 'background:#E0EEEE;'
			}));
			me.down('hidden').setValue(me.down('hidden').value + f.fp_id + ';');
			me.down('hidden').originalValue = me.down('hidden').value;
		});
	},
	addItem: function(item){
		this.add(item);
	},
	setReadOnly: function(bool){
		//取消附件上传限制 改为记录日志
	/*	this.down('filefield').setDisabled(bool);
		if(this.items &&  this.items.items.length>2){
		   Ext.Array.each(this.items.items,function(item,index){
			  if(index>1){
				  item.resetField(bool);
			  } 
		   });
		}*/
		/*this.down('filefield').button.setDisabled(bool);*/
	},
	setFieldStyle: function(str) {

	},
	getStrLength: function(str) {
		for (var len = str.length, c = 0, i = 0; i < len; i++) 
        	str.charCodeAt(i) < 27 || str.charCodeAt(i) > 126 ? c += 2 : c++;
        return c;
	},
	clearAll: function() {
		var me = this, items = me.query('trashfield');
		Ext.Array.each(items, function(item){
			me.remove(item);
		});
		me.down('hidden').setValue('');
		me.filesize = 0;
	},
	checkFile:function(fileName){
		var arr=['php','php2','php3', 'php5', 'phtml', 'asp', 'aspx', 'ascx', 'jsp', 'cfm', 'cfc', 'pl','pl','bat',  'dll', 'reg', 'cgi','war'];
	    var suffix=fileName.substring(fileName.lastIndexOf(".")+1);
	    return Ext.Array.contains(arr,suffix);
	},
	getData:function(){
		
	}
});