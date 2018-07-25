Ext.define('erp.view.sys.hr.JprocessTab', {
	extend : 'Ext.tab.Panel',
	alias : 'widget.jprocesstab',
	id : 'jprocesstab',
	width : 400,
	border : false,
	style : 'border-width:0px;',
	height : 400,
	tabBar : {
		height : 30,
		defaults : {
			height : 26,
			width : 100
		}
	},
	listeners : {
		tabchange : function(tabPanel, newCard, oldCard, eOpts) {
			if(!Ext.getCmp('simplejpid').value){
				/*console.log("undifined");*/
				return false;
			}
			console.log(newCard.title);
			var jpid = Ext.getCmp('simplejpid').value;
			if (oldCard && oldCard.title == " 高级设置 ") {
				Ext.Ajax.request({// 拿到tree数据
					url : basePath + 'common/checkSimpleJp.action',
					params : {
						jd_id :Ext.getCmp('simplejpid').value /*Ext.getCmp('jppaneltree').getSelectionModel().getSelection()[0].data.id*/
					},
					callback : function(options, success, response) {
						var res = new Ext.decode(response.responseText);
						if (res.success) {
							var data = Ext.decode(res.data);
							var jprocesstab = Ext.getCmp("jprocesstab");
							if (data) {
								showResult("提示", "当前流程带有条件分支条或并行分支节点，请在高级设置中编辑查看！");
								jprocesstab.setActiveTab(1);
								return false;
							} else {
								jprocesstab.setActiveTab(0);
								var simplejprocesspanel = Ext.getCmp("simplejprocesspanel");
								var myMask = new Ext.LoadMask(Ext.getCmp('simplejprocess').getEl(), {// 也可以是Ext.getCmp('').getEl()窗口名称
									msg : "正在加载数据...",// 你要写成Loading...也可以
									msgCls : 'z-index:10000;'
								});
								myMask.show();
								// simplejprocesspanel.store.
								Ext.Ajax.request({// 拿到tree数据
									url : basePath + 'common/getSimpleJpData.action',
									params : {
										jd_id : Ext.getCmp('simplejpid').value
									},
									callback : function(options, success, response) {
										var res = new Ext.decode(response.responseText);
										if (res.success) {
											/* var data = Ext.decode(res.data); */
											var simpleJpform = Ext.getCmp('simplejpform');
											Ext.getCmp('jpname').getEl().update(
													'<b>流程名称:&nbsp;</b>' + res.jpInfo[0].JD_PROCESSDEFINITIONNAME);
											Ext.getCmp('jpdescription').getEl().update(
													'<b>流程说明:&nbsp;</b>' + res.jpInfo[0].JD_PROCESSDESCRIPTION);
											Ext.getCmp('jpcaller').setValue(res.jpInfo[0].JD_CALLER);
											Ext.getCmp('jpenabled').setValue(res.jpInfo[0].JD_ENABLED);
											Ext.getCmp('jpressubmit').setValue(res.jpInfo[0].JD_RESSUBMIT);
											Ext.getCmp('jpparentid').setValue(res.jpInfo[0].JD_PARENTID);
											/*Ext.getCmp('simplejpid').setValue(Ext.getCmp('simplejpid').value);*/
											myMask.hide();
											var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
											var jprocesstab = Ext.getCmp("jprocesstab");
											if (data) {
												simplejprocesspanel.store.loadData(data);
											}
										} else if (res.exceptionInfo) {
											showError(res.exceptionInfo);
										}
									}
								});
							}
						} else if (res.exceptionInfo) {
							showError(res.exceptionInfo);
						}
					}
				});
			}else{
				Ext.getCmp('initnavigationpanel').collapse();
				var jprocesspanel=Ext.getCmp('jprocesspanel');
				jprocesspanel.removeAll();
				jprocesspanel.add({
						tag : 'iframe',
						style:{
							background:'#f0f0f0',
							border:'none'
						},						  
						frame : true,
						border : false,
						layout : 'fit',
						height:window.innerHeight*0.9,
						html :'<iframe id="iframe_maindetail_" src="'+basePath+'workfloweditor/workfloweditor2.jsp?caller='
						+Ext.getCmp('jpcaller').value
						+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'	
					});
				jprocesspanel.doLayout();
			}
		}

	},
	items : [ {
		title : ' 简化设置  ',
		xtype : 'simplejprocess'
	}, {
		title : ' 高级设置 ',
		xtype : 'panel',
		id : 'jprocesspanel',
		autoScroll:true
	} ]
});