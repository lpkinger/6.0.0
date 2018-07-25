Ext.QuickTips.init();

Ext.define('erp.view.common.bench.BenchFormPanel', {
	extend:'Ext.form.Panel',
	alias:'widget.erpBenchFormPanel',
	requires: ['erp.view.core.button.Add'],
	bodyCls : 'x-panel-body-gray',
	margin:'0',
	padding:'0px',
	id:'benchform',
	cls:'form',
	title:'工作台',
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){ 
		Ext.apply(this, { 
			items:[{
				xtype:'container',
				layout:'table',
				id:'business',
				items:[{
					xtype:'displayfield',
					margin:'0 0 0 5',
					value:'<b>业务:</b>'
				}]
			}]
		});
		var param = {bccode: this.bench || bench, condition: (getUrlParam('urlcondition') || this.condition), _noc: (getUrlParam('_noc') || this._noc),_config:getUrlParam('_config')};
		if(this.getBenchForm) this.getBenchForm('bench/getBench.action', this.params || param);

		this.callParent(arguments);
	},
	getBenchForm: function(url, param){
		var me = this, tab = me.FormUtil.getActiveTab();
		me.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params: param,
			method : 'post',
			callback : function(options, success, response){
				me.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				if(res.bench){
					var bench1 = res.bench;
					me.bc_id = bench1.bc_id;
					//title
					/*if(bench.bc_title && bench.bc_title != ''){
						me.setTitle(bench.bc_title);
					    if(tab && tab.id!='HomePage') {
							try {
				                tab.setTitle(bench1.bc_title);
				            } catch (e) {
				            }
					    }				     
					}*/
					var title = bench1.bc_title;
					me.setTitle(null);
					me.setBenchButton('basicData',bench1.benchButtons.basicData);
					me.setBenchButton('makeOrder',bench1.benchButtons.makeOrder);
					me.setBenchButton('moreOperat',bench1.benchButtons.moreOperat);
					me.down('header').add({	
						xtype: 'button',
						text: '报表查询',
						width: 85,
						cls:'x-btn-top',
						iconCls: 'btn-reportsquery',
						handler: function(btn){
							 var title = parent.Ext.getCmp('content-panel').getActiveTab().title;
							 openUrl2('jsps/common/reportsQuery.jsp?code='+ bench +'&title='+title,'报表查询');
						}
					},{	
						xtype: 'button',
						text: '权限申请',
						width: 85,
						cls:'x-btn-top',
						iconCls: 'btn-powerapply',
						hidden:true,
						handler: function(btn){
							Ext.create('erp.view.common.bench.SelfBusinessSet');
						}
					},{	
						xtype: 'button',
						cls:'x-btn-top',
						iconCls: 'btn-agencyset',
						text: '待办设置',
						width: 85,
						handler: function(btn){
							Ext.create('erp.view.common.bench.SelfSceneSet');
						}
					},{
						xtype: 'button',
						text: '业务流程图',
						width: 100,
						margin: em_type == 'admin'?'0 5 0 0':'0 1000% 0 0',
						cls:'x-btn-top',
						iconCls:'btn-flowchart',
						listeners:{
							click:function(btn){
								Ext.create('erp.view.common.bench.BenchFlowChartWindow',{
									benchId: me.bench || bench
								});
							}
						}
					});
					
					if(em_type == 'admin'){
						me.down('header').add({	
							xtype: 'button',
							text: '工作台设置',
							width: 100,
							margin: '0 1000% 0 0',
							cls:'x-btn-top',
							iconCls: 'btn-benchset',
							handler: function(btn){
								 openUrl2('jsps/ma/bench/singleBenchSet.jsp?benchcode='+ bench, title+'工作台设置');
							}
						});
					}
					if(bench1.benchBusinesses&&bench1.benchBusinesses.length>0){
						me.setBussinesses(bench1.benchBusinesses,bench1.hideBusinesses);
					}
				}else{
					showError('工作台未配置！');
				}
				
				me.fireEvent('afterload', me);
			}
		});
	},
	setBenchButton: function(type,buttons){
		var me = this;
		var menus = new Array();
		if(buttons.length<1){
			return;
		}
		Ext.Array.each(buttons,function(button){
			var objs = new Array();
			Ext.Array.each(button.menuButtons,function(btn){
				var obj = {	
					id:btn.bb_code,
					minWidth:50,
					text : btn.bb_text,
					backIconCls : btn.bb_url ? 'x-menuitem-add' : '',
					handler : function(b,event){
						if(event.target && event.target.classList.contains('x-menuitem-add')) {
				    		openUrl2(parseUrl(btn.bb_url),btn.bb_text);
				    	}else {
				    		openUrl2(parseUrl(!btn.bb_listurl&&btn.bb_caller?'jsps/common/datalist.jsp?whoami='+btn.bb_caller:btn.bb_listurl),btn.bb_text);
				    	}
					}
				};
				objs.push(obj);
			});
			var obj = {	
				id:button.bb_code,
				minWidth:50,
				text : button.bb_text,
				backIconCls : button.bb_url ? 'x-menuitem-add' : '',
				handler : function(btn,event){
					if(button.bb_code){
						if(event.target && event.target.classList.contains('x-menuitem-add')) {
				    		openUrl2(parseUrl(button.bb_url),button.bb_text);
				    	}else {
				    		openUrl2(parseUrl(!button.bb_listurl&&button.bb_caller?'jsps/common/datalist.jsp?whoami='+button.bb_caller:button.bb_listurl),button.bb_text);
				    	}
					}
				}
			};
			if(objs.length>0){
				obj.menu = new Ext.menu.Menu({
					showSeparator: false,
					items:objs,
					bodyCls: 'x-bench-menu-body',
					maxHeight:(Ext.isIE?screen.height:window.innerHeight)*0.8,
					listeners: {
						mouseleave: function() {
							this.hide();
						}
					}
				});
			}
			
			menus.push(obj);
		});
		if(type=='basicData'){
			me.down('header').add({
				xtype: 'button',
				text: '基础资料',
				width: 85,
				cls:'x-btn-top',
				iconCls: 'btn-basicdata',
				iconCls:'x-button-icon-benchadd',
				listeners:{
					mouseover:function(btn){
						btn.showMenu();	
					},
					mouseout: function(btn) {
						setTimeout(function() {
							if(!btn.menu.over) {
								btn.hideMenu();
							}
	                    }, 20);
					}
				},
				menu:new Ext.menu.Menu({
					showSeparator: false,
					items:menus,
					bodyCls: 'x-bench-menu-body',
					maxHeight:(Ext.isIE?screen.height:window.innerHeight)*0.8,
					listeners: {
						mouseover: function() {
							this.over = true;
						},
						mouseleave: function() {
							this.over = false;
							this.hide();
						}
					}
				})
			});
		}else if(type=='makeOrder'){
			me.down('header').add({
				xtype: 'button',
				text: '业务制单',
				cls:'x-btn-top',
				iconCls: 'btn-makeorder',
				width: 90,
				listeners:{
					mouseover:function(btn){
						btn.showMenu();	
					},
					mouseout: function(btn) {
						setTimeout(function() {
							if(!btn.menu.over) {
								btn.hideMenu();
							}
	                    }, 20);
					}
				},
				menu:new Ext.menu.Menu({
					showSeparator: false,
					items:menus,
					bodyCls: 'x-bench-menu-body',
					maxHeight:(Ext.isIE?screen.height:window.innerHeight)*0.8,
					listeners: {
						mouseover: function() {
							this.over = true;
						},
						mouseleave: function() {
							this.over = false;
							this.hide();
						}
					}
				})
			});
		}else if(type=='moreOperat'){
			me.down('header').add({
				xtype: 'button',
				text: '更多操作',
				iconCls: 'btn-moreoperation',
				width: 90,
				listeners:{
					mouseover:function(btn){
						btn.showMenu();	
					},
					mouseout: function(btn) {
						setTimeout(function() {
							if(!btn.menu.over) {
								btn.hideMenu();
							}
	                    }, 20);
					}
				},
				menu:new Ext.menu.Menu({
					showSeparator: false,
					items:menus,
					bodyCls: 'x-bench-menu-body',
					maxHeight:(Ext.isIE?screen.height:window.innerHeight)*0.8,
					listeners: {
						mouseover: function() {
							this.over = true;
						},
						mouseleave: function() {
							this.over = false;
							this.hide();
						}
					}
				})
			});
		}
		
	},
	setBussinesses: function(buttons,buttons1){
		var me = this;
		var container = me.down('#business'),card = Ext.getCmp('businesses');
		var businesses = new Array(),panels = new Array();
		var hasActive = false;
		Ext.Array.each(buttons,function(button,index){
			var active = (business&&business ==button.bb_code) || !hasActive&&button.count>0;
			if(active){
				if(scene) button.active = scene;
				hasActive = true;
			}
			businesses.push({	
				xtype: 'erpStatButton1',				
				text: button.bb_name,
				id: 'busin_'+button.bb_code,
				data: button,
				minWidth: 72,
				stat: button.count,
				active: active
			});
			var scenes = new Array();
			Ext.Array.each(button.benchScenes,function(scene,index){
				scenes.push({	
					xtype: 'erpStatButton1',				
					text: scene.bs_title,
					id: scene.bs_code,
					data: scene,
					minWidth: 72,
					show: scene.bs_iscount!=0,
					stat: scene.count,
					active: false
				});
			});
			panels.push({
				xtype : 'panel',
				layout : 'border',
				id: 'business_'+button.bb_code,
				items: [{
					xtype: 'erpBusinessFormPanel',
					scenes: scenes
				},{
					region:'center',
					xtype:'panel',
					layout:'card',
					id:'scenes_'+button.bb_code
				}],
				listeners: {
					activate: function(panel){
        				var activepanel = panel.items.items[1].getLayout().getActiveItem();
        				if(activepanel){
        					activepanel.fireEvent('activate',activepanel);
        				}
					}
				}
			});
		});
		if(!hasActive&&!businesses){
			businesses[0].active = true;
		}
		if(buttons1.length>0){
			var menus = new Array();
			var activeBtn = null;
			Ext.Array.each(buttons1,function(button){
				var scenes = new Array();
				var active = !hasActive&&businesses&&businesses ==button.bb_code;
				if(active){
					if(scene) button.active = scene;
					hasActive = true;
					activeBtn = button;
					scene = null;
				}
				Ext.Array.each(button.benchScenes,function(scene,index){
					scenes.push({	
						xtype: 'erpStatButton1',				
						text: scene.bs_title,
						id: scene.bs_code,
						data: scene,
						minWidth: 72,
						show: scene.bs_iscount!=0,
						stat: scene.count,
						active: false
					});
				});
				var obj = {	
					id:'busin_'+button.bb_code,
					text: button.bb_name,
					data: button,
					scenes: scenes,
					cls:'x-btn-top',
					minWidth:70,
					scenes: scenes
				};
			
				if(!obj.handler){
					obj.handler = function(business,e){
						var businesses1 = Ext.getCmp('businesses');
		   				var Business = business.data.bb_code;
		   				var businessid = 'business_'+Business;
		   				var businessPanel = businesses1.down('#'+businessid);
		   				if(!businessPanel){
		   					businessPanel = card.add({
								xtype : 'panel',
								layout : 'border',
								id: 'business_'+button.bb_code,
								items: [{
									xtype: 'erpBusinessFormPanel',
									scenes: business.scenes
								},{
									region:'center',
									xtype:'panel',
									layout:'card',
									id:'scenes_'+button.bb_code,
									activeItem: 0
								}],
								listeners: {
									activate: function(panel){
				        				var activepanel = panel.items.items[1].getLayout().getActiveItem();
				        				if(activepanel){
				        					activepanel.fireEvent('activate',activepanel);
				        				}
									}
								}
							});
		   				}
		   				var scenes = businessPanel.down('erpBusinessFormPanel erpSwitchButton');
		   				var activeBtn = scenes.getActive();
		   				if(!activeBtn){
		   					var btn = scenes.down('#'+business.data.active);
		   					scenes.setActive(btn);
		   				}
		   				businesses1.layout.setActiveItem(businessid);
		   				var more = Ext.getCmp('more');
		   				more.data = business.data;
		   				more.noactive = false;
		   				Ext.getCmp('switch').setActive(more);
					};
				}
				menus.push(obj);
				
			});
			businesses.push({
				xtype: 'button',
				id: 'more',
				noactive: activeBtn==null?true:false,
				text: '更多业务',
				data: activeBtn,
				active:activeBtn!=null,
				listeners:{
					mouseover:function(btn){
						btn.showMenu();	
					},
					mouseout: function(btn) {
						setTimeout(function() {
							if(!btn.menu.over) {
								btn.hideMenu();
							}
	                    }, 20);
					}
				},
				menu:new Ext.menu.Menu({
					showSeparator: false,
					items:menus,
					bodyCls: 'x-bench-menu-body',
					listeners: {
						mouseover: function() {
							this.over = true;
						},
						mouseleave: function() {
							this.over = false;
							this.hide();
						}
					}
				})
			});
		}
		card.add(panels);
		if(!hasActive){
			businesses[0].active = true;
		}
		container.add({
			xtype: 'erpSwitchButton',
			id: 'switch',
			items: businesses
		});
		
	}
});
