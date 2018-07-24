Ext.define('erp.view.ma.bench.SingleBenchSet', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	hideBorders : true,
	initComponent : function() {
		this.loadScenes();
		this.callParent(arguments);
	},
	loadScenes : function() {
		var me = this;
   		var bccode = benchcode;
		Ext.Ajax.request({
			url : basePath + 'bench/ma/getBenchScenes.action',
			params : {
				bccode: bccode
			},
			async: false,
			merthod:'POST',
			callback : function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				var items = new Array();
				if(res.success){
					if (res.scenes.length>0) {
						Ext.Array.each(res.scenes,function(scene){
							items.push({
								title : scene.bstitle,
								border : false,
								id : scene.bscode,
								layout : 'fit',
								listeners : {
									activate : function(p){
										if(p.body) {
											var iframe = p.getEl().down('iframe');
											if(!iframe) {
												var body = p.getEl().child('.x-panel-body',true);
												iframe = document.createElement('iframe');
												iframe.setAttribute("id", 'iframe_scene_'+scene.bscode);
												iframe.setAttribute("src", basePath+"jsps/ma/bench/sceneSet.jsp?bench="+bccode+"&formCondition=bs_codeIS'" + scene.bscode+"'&gridCondition=sg_bscodeIS'" + scene.bscode+"'");
												iframe.setAttribute("height", "100%");
												iframe.setAttribute("width","100%");
												iframe.setAttribute("frameborder", 0);
												iframe.setAttribute("scrolling", "auto");
												body.appendChild(iframe);
											}
										}
									}
								}
							});
						});
					} else {
						showError('此工作台未配置场景！');
						items.push({
							tag : 'iframe',
							title : '新场景',
							id : bccode+'_newScene',
							border : false,
							layout : 'fit',
							html : '<iframe src="'+basePath+'jsps/ma/bench/sceneSet.jsp?bench='+bccode+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'						
						});
					}
				}else if (res.exceptionInfo) {
					showError(res.exceptionInfo);
				}
				Ext.apply(me, {
					items : [{
						xtype : 'erpTabPanel',
						id : 'bench_'+bccode,
						title : '工作台设置',
						items : items,
						maxDetno : res.maxDetno,
						plugins:[new Ext.ux.TabScrollerMenu({
							pageSize: 10,
				            maxText  : 15,
				            search : true
				        })],
				        tools : [{
							xtype: 'button',
							text:'工作台按钮',
							id:'benchbtn',
							cls: 'x-btn-gray',
							iconCls:'x-button-icon-code',
							style:'margin-right:10px'
						}]
					}]
				});
			}
		});
	}
});