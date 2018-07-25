Ext.define('erp.view.plm.task.plugin.DocFileField', {
	extend: 'Ext.form.field.Trigger',
	alias: 'widget.prjdocfield',
	triggerCls: 'x-form-search-trigger',
	triggerName:null,
	title:null,
	url:null,
	nodeId:null,
	filetrees:null,
	initComponent: function() {
		var me = this;
		me.addEvents({
			aftertrigger: true,
			beforetrigger: true
		});
		me.callParent(arguments);
		if(!me.ownerCt) {
			Ext.defer(function(){
				me.getOwner();
			}, 50);
		}
		if(me.clearable) {
			me.trigger2Cls = 'x-form-clear-trigger';
			if(!me.onTrigger2Click) {
				me.onTrigger2Click = function(){
					this.setValue(null);
				};
			}
		}
	},
	getOwner: function() {
		var me = this;
		if (me.el) {
			var gridEl = me.el.up('.x-grid');
			if (gridEl) {
				var grid = Ext.getCmp(gridEl.id);
				if (grid) {
					me.owner = grid;
					me.column = grid.down('gridcolumn[dataIndex=' + me.name + ']');
				}
			}
		}
	},
	onTriggerClick: function() {
		var trigger = this,
		bool = true; // 放大镜所在	
		bool = trigger.fireEvent('beforetrigger', trigger);
		if (bool == false) {
			return;
		}
		this.setFieldStyle('background:#C6E2FF;');

		BaseCondition = '',
		treeCondition = '',
		findConfig = this.findConfig;
		window.onTriggerClick = this.id;
		// 存在查询条件的字段
		if (findConfig) {
			treeCondition = (typeof findConfig == 'function' ? findConfig.call(null) : findConfig);
		}
		if (this.BaseCondition) {
			BaseCondition = this.BaseCondition;
		}
		trigger.createWindow();
	},
	createWindow:function(){
		var trigger = this;
		var width = Ext.isIE ? screen.width * 0.7 * 0.4 : '50%',
				height = Ext.isIE ? screen.height * 0.9 : '90%';
		//针对有些特殊窗口显示较小
		width =this.winWidth ? this.winWidth:width;
		height=this.winHeight ? this.winHeight:height;
		Ext.Ajax.request({
			url:basePath + 'plm/project/getProjectFileTree.action',
			params:{
				condition:"pd_prjid="+prjplanid,
				checked:'true'
			},
			method:'post',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					if(res.tree){
						var gantt=Ext.getCmp('gantt_panel'),lockedgrid=gantt.lockedGrid,rec=lockedgrid.getSelectionModel().selected.items[0];
						var store =Ext.create('Ext.data.TreeStore', {
							fields:['pd_name','pd_taskname'],
							root : {
								text : 'Root',
								id : 0,
								expanded : true
							}
						})
						store.setRootNode({
							text: 'root',
							id: 'root',
							expanded: true,
							children: res.tree
						});	
						trigger.setChecked(rec,store);
						var tree = Ext.create('Ext.tree.Panel', {		
							rootVisible : false,
							autoScroll:true,
							hideHeaders : true,
							border:false,
							store:store,
							columns : [{
								xtype : 'treecolumn',
								dataIndex:'pd_name',
								renderer:function(val,meta,record){
									if(record.data.pd_taskname){
										return val+'&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:gray">[关联任务:'+record.data.pd_taskname+'] </span>'
									}else return val;
								},
								sortable : true,
								flex : 1
							}]
						});
						var trwin = new Ext.window.Window({
							title:'文件信息  ['+rec.data.Name+']',
							height: height,
							width: width,
							buttonAlign: 'center',
							layout: 'fit',
							modal:true,
							rec:rec,
							recValues:rec.data.prjdocid,
							items: [tree],
							buttons: [{
								text: '确认',				
								handler: function(btn) {									
									trigger.setValues(btn);
									btn.ownerCt.ownerCt.close();
								}
							},{
								text: '关  闭',
								handler: function(btn) {
									btn.ownerCt.ownerCt.close();
								}
							}]
						});
						trwin.show();
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
			}
		});

	},
	setValues:function(btn){
		var trigger = this,win=btn.ownerCt.ownerCt,tree = btn.ownerCt.ownerCt.down('treepanel'),checks = tree.getChecked();
		var docname='',docid='';
		if(checks.length>0){
			for(var i=0;i<checks.length;i++){
				docname+=checks[i].get('pd_name').replace(/,/g,'，')+',';
				docid+=checks[i].get('id')+',';
			}
		}
		if(docname.length>1){
			docname=docname.substring(0,docname.length-1);
			docid=docid.substring(0,docid.length-1);
		}		
		if(docid!=win.recValues){
			Ext.Ajax.request({
				url : basePath + "plm/gantt/setDoc.action",
				params:{
					prjId:prjplanid,
					docName:docname,
					docId:docid,
					taskId:win.rec.getId()
				},
				method : 'post',
				callback : function(options,success,response){
					var localJson = new Ext.decode(response.responseText);
					if(localJson.success){
						win.rec.set('prjdocname',docname);
						win.rec.set('prjdocid',docid);
						win.rec.commit();
					}else if(localJson.exceptionInfo) alert(localJson.exceptionInfo);
				}
			});		
		}		
	},
	setChecked:function(rec,store){
		var node;
		if(rec &&  rec.data['prjdocid']!=null){
			var ids=rec.data['prjdocid'].split(',');
			Ext.each(ids,function(id){
				node=store.getNodeById(id);
				if(node) node.data.checked=true;
			})

		}
	},
	getCondition: function(triggerCond) {
		var condition = [], findConfig = this.findConfig;
		// 存在查询条件的字段
		if (findConfig) {
			condition.push(typeof findConfig == 'function' ? findConfig.call(null) : findConfig);
		}
		if (this.BaseCondition) {
			condition.push(this.BaseCondition);
		}

		triggerCond && (condition.push(triggerCond));
		return condition.join(" AND ");
	}
});