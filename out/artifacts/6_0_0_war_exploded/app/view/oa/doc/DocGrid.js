Ext.define('erp.view.oa.doc.DocGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.docgrid',
	id:'docgrid',
	columnLines : true,
//	autoScroll : true,
	margin:'5px 0px 20px 0px',
	scroll:false, 
	resizable:false,
	viewConfig: {
		style: { overflow: 'auto', overflowX: 'hidden' }
	},
	defaults:{
	},
	columns:[{
		text:'ID',
		dataIndex:'dl_id',
		width:50,
		hidden:true
	},{
		text: '文档名称',
		dataIndex: 'dl_name',
		flex: 1,
		cls:'x-grid-header-1',
		readOnly:true/*,
		renderer:function(val, meta, record){
	        var style=record.data.dl_style;
			if(style!='目录')  return '<img src="'+basePath+'jsps/oa/doc/resources/images/docico/icon-'+style+'.gif">' + val;			
			else  return '<img src="'+basePath+'resource/images/icon/folder_Close.gif">'+val;
		}*/
	},{
		text: '锁定状态',
		dataIndex: 'dl_locked',
		width:100,
		cls:'x-grid-header-1',
		readOnly:true,
		renderer:function(val, meta, record){
			if(val==-1)  return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/lockdoc.png">';
			else return '';
		}
	},{
		text: '文件类型',
		dataIndex: 'dl_style',
		width:100,
		cls:'x-grid-header-1',
		readOnly:true,
		renderer:function(val, meta, record){
	        var style=record.data.dl_style.toLowerCase();
			if('bmp,jpg,png,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,wmf'.indexOf(style)>= 0) 
				return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/image.png">' ;
			else if('doc,docx,wps'.indexOf(style)>= 0)
				return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/word.png">' ;
			else if('mp3,wma,rm,wav,midi,ape,flac'.indexOf(style)>= 0)
				return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/music.png">' ;
			else if('xls,xlsx,xlsb,et,eet,xlt,xlsm'.indexOf(style)>= 0)
				return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/excel.png">' ;
			else if('txt,pdf'.indexOf(style)>= 0)
				return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/'+style+'.png">' ;
			else if('ppt,pptx,dps'.indexOf(style)>= 0)
				return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/ppt.png">' ;
			else if('zip,rar'.indexOf(style)>= 0)
				return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/rar.png">' ;
			else if (style=='目录')
				return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/dir.png">' ;
			else  return '<img src="'+basePath+'jsps/oa/doc/resources/images/images/docico/others.png">';
		}
	},{
		text:'文件大小',
		dataIndex: 'dl_size',
		width:100,
		cls:'x-grid-header-1',
		readOnly:true,
		renderer:function(val,meta,record){
			if(record.data.dl_kind==-1){
				return "/";
			}else {
				var f = parseFloat(val);              
				if (isNaN(f)) {   
					return;              
				}              
				f = Ext.util.Format.number(Math.round(val*100)/(100*1024*1024),'0.00');              
				return f+" MB";          
			}
		}
	},{
		text:'上传时间',
		dataIndex:'dl_createtime',
		cls:'x-grid-header-1',
		readOnly:true,
		width:150,
		xtype:"datecolumn",
	    format:"Y-m-d H:i:s"
	},{
		text:'上传者',
		dataIndex:'dl_creator',
		cls:'x-grid-header-1',
		width:100,
		readOnly:true
	},{
		text:'是否文件夹',
		dataIndex: 'dl_kind',
		cls:'x-grid-header-1',
		width:0,
		border:0,
		flex: 0,
		readOnly:true
	},{
		xtype: 'actioncolumn',
		header:'详情',
		cls:'x-grid-header-1',
		width:80,
		border:0,
		flex: 0,
		icon: basePath+'jsps/oa/doc/resources/images/images/docico/icon-log.png',
		handler: function(view, rowIndex, colIndex, item, e){
			var grid = Ext.getCmp('docgrid');
			var dlId = grid.getStore().data.items[rowIndex].data.dl_id;
			grid.showLogWin(dlId);
		}
	}],
	store: Ext.create('Ext.data.Store', {
		fields: [{
			name: 'dl_id',
			type: 'number'
		},{
			name:'dl_name',
			type:'string'
		},{
			name: 'dl_locked',
			type: 'number'
		},{
			name:'dl_style',
			type:'string'
		},{
			name:'dl_size',
			type:'float'
		},{
			name:'dl_createtime',
			type:'date'
		},{
			name:'dl_creator',
			type:'string'
		},{
			name:'dl_kind',
			type:'number'
		}],
		sorters: [{
			property : 'dl_createtime',
			direction: 'DESC'
		}]
	}),
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		/*listeners:{
			'select': function(selModel, record){
				//selModel.view.ownerCt.selectAllPower(record);
			},
			'deselect': function(selModel, record){
				//selModel.view.ownerCt.deselectAllPower(record);
			}
		},*/
		onHeaderClick: function(headerCt, header, e) {
			var grid=Ext.getCmp('docgrid');
			if (header.isCheckerHd) {
				var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
				if (!isChecked) {
					/*grid.store.each(function(item){
						grid.selectAllPower(item);
					});*/
					grid.selModel.selectAll();
				} else {
					/*grid.store.each(function(item){
						grid.deselectAllPower(item);
					});*/
					grid.selModel.deselectAll();
				}
			} 

		}
	}),
	initComponent: function(){
		this.addEvents(
				'rowdblclick',
				'select',
				'cellclick'
		);
		this.callParent(arguments);
		this.on('selectionchange', this.onSelect, this);
		this.on('cellclick', this.updateDataToTab);
		
	},
	updateDataToTab:function(thisp,row,col,record){
		if(col!=0){
			var doctabs=Ext.getCmp('doctab');
			doctabs.fireEvent('tabItemChange',doctabs,record);
		    var docpanel=Ext.getCmp('docpanel');
		    docpanel.currentItem=record;
		    var lockstatus = Ext.getCmp('lockstatus');
			var lockdocbtnvalue = Ext.getCmp('docgrid').getSelectionModel().getSelection()[0].data.dl_locked;
		    var lockdoc=Ext.getCmp('lockdoc'),
		    buttontext=lockdocbtnvalue==-1?'解锁':'锁定';
		    lockstatustext=lockdocbtnvalue==-1?'已锁定':'未锁定';
		    lockdoc.setText(buttontext);
			lockstatus.setValue(lockstatustext);
		    docpanel.reSetButton(docpanel);
		}
	},
	getItemData: function(parentId,record){
		var me = this;
		me.setLoading(true);
		var condition=parentId==5?"dl_statuscode='DELETED'":"dl_statuscode='AUDITED'";
		Ext.Ajax.request({
			url : basePath + 'oa/docuemtlist/getDocumentsByParentId.action',
			params: {
				parentId:parentId,
				condition:condition,
				_self: false
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exception || res.exceptionInfo){
					showError(res.exceptionInfo);
					return;
				}
				me.store.loadData(res.docs);
				if(record!=null) 
				{
					me.getSelectionModel().select(record.index);
					var lockstatus = Ext.getCmp('lockstatus');
					var lockdocbtnvalue = Ext.getCmp('docgrid').getSelectionModel().getSelection()[0].data.dl_locked;
				    var lockdoc=Ext.getCmp('lockdoc');
				    var docpanel=Ext.getCmp('docpanel');
				    buttontext=lockdocbtnvalue==-1?'解锁':'锁定';
				    lockstatustext=lockdocbtnvalue==-1?'已锁定':'未锁定';
				    lockdoc.setText(buttontext);
					lockstatus.setValue(lockstatustext);
					docpanel.reSetButton(docpanel);
				}
				
			}
		});
	},
	onRowDblClick: function(view, record, item, index, e) {
		this.fireEvent('rowdblclick', this, this.store.getAt(index));
	},
	onSelect: function(model, selections){
		var selected = selections[0];
		if (selected) {
			this.fireEvent('select', this, selected);
		}
	},
	onLoad: function(){
		this.getSelectionModel().select(0);
	},
	loadFeed: function(url){
		var store = this.store;
		store.getProxy().extraParams.feed = url;
		store.load();
	},
	formatTitle: function(value, p, record){
		return Ext.String.format('<div class="topic"><b>{0}</b><span class="author">{1}</span></div>', value, record.get('author') || "Unknown");
	},
	showLogWin: function(dlId){
		var win = Ext.getCmp('logWin');
		if(!win){
			win = Ext.create('Ext.window.Window', {
				title: '操作日志',
				width: '60%',
 			    height: '70%',
 			    closeAction: 'hide',
 			    id:'logWin',
 			    layout:'fit',
 			    listeners:{
 			    	hide:function(win){
 			    		Ext.getBody().unmask();
 			    		win.destroy();
 			    	},show:function(){
 			    		Ext.getBody().mask();
 			    	}  
 			    },
 			    bbar: ['->',{
 			    	xtype: 'button',
 			    	cls:'x-btn-close',
 			    	text: '关闭',
 			    	handler: function(){
 			    		Ext.getCmp('logWin').close();
 			    	}
 			    },'->'],
 			    items: [{
 			    	xtype: 'erpDocLogPanel',
 			    	dlId: dlId
 			    }],
			});
		}
		win.show();
	}
});

