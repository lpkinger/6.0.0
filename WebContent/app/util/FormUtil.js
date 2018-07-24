/**
 * 与formpanel操作相关的增删改查的操作方法都在这里哦
 * 如果formpanel作为主表，还有一些gridpanel等的信息作为附表，form操作时还包括了对gridpanel等的操作
 * @author yingp
 */
Ext.define('erp.util.FormUtil',{
	/**
	 * 从后台拿到formpanel配置
	 * @param form formpanel表
	 * @param url 提交的action名
	 * @param param 传递回去的数据，比如{caller:Purchase,condition:pu_id=30001}
	 */
	takeoveraction:'common/takeOverTask.action',
	deleteProcess:'common/deleteProcessAfterAudit.action?_noc=1',
	getItemsAndButtons: function(form, url, param){
		var me = this, tab = me.getActiveTab();
		me.setLoading(true);
		Ext.Ajax.request({//拿到form的items
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
				form.fo_id = res.fo_id;
				form.fo_keyField = res.keyField;
				form.tablename = res.tablename;//表名
				if(res.keyField){//主键
					form.keyField = res.keyField;
				}
				if(res.statusField){//状态
					form.statusField = res.statusField;
				}
				if(res.statuscodeField){//状态码
					form.statuscodeField = res.statuscodeField;
				}
				if(res.codeField){//Code
					form.codeField = res.codeField;
				}
				if(res.dealUrl){
					form.dealUrl = res.dealUrl;
				}
				if(res.mainpercent && res.detailpercent){
					form.mainpercent = res.mainpercent;
					form.detailpercent = res.detailpercent;
				}
				form.fo_isPrevNext = res.fo_isPrevNext=='1'?true:false;//上一条下一条
				form.fo_detailMainKeyField = res.fo_detailMainKeyField;//从表外键字段
				//data&items
				var items = me.setItems(form, res.items, res.data, res.limits, {
					labelColor: res.necessaryFieldColor
				});								
				// UI新布局
				// 判断是单表还是主从表
				var single = false;
				if(!(res.detailkeyfield&&res.fo_detailMainKeyField)){
					single = true
				}
				me.addItemsForUI(form,items,single);
				//title
				if(res.title && res.title != ''){
					form.title = res.title;
					//特殊tab界面需要载入后台传入的title 流程界面显示标题
					if(jspName == 'ma/jprocess/JprocessTemplateSet'){
						form.setTitle(res.title)
					}
					if(form.source=='allnavigation'){
						form.setTitle(res.title+'<font color=red>[界面展示]</font>');
					}
					var _tt = res.title;
					if(form.codeField) {
						var _c = form.down('#' + form.codeField);
						if( _c && !Ext.isEmpty(_c.value) )
							_tt += '(' + _c.value + ')';
					}
					var win = parent.Ext.getCmp('win');
					if(win){
						try {
			                win.setTitle(_tt);
			            } catch (e) {
			            }
					}else if(tab && tab.id!='HomePage') {
						try {
			                tab.setTitle(_tt);
			            } catch (e) {
			            }
				    }
				}
				if(!form._nobutton){
					if(jspName == 'oa/doc/documentmanage'){
						//文旦管理目录字段bug
						Ext.each(form.items.items, function(item, index){
							item.labelAlign = 'right';
						});	
						form.setButtonsOld(form, res.buttons);
					}else{
						me.setButtons(form, res.buttons);
					}
				}
				//form第一个可编辑框自动focus
				me.focusFirst(form);
				form.fireEvent('afterload', form);
				
				// 2018050469  单据关闭二次确认逻辑  zhuth  界面加载出来后重设所有字段dirty状态
				Ext.defer(function(){
					var fields = form.getForm().getFields().items;
					Ext.Array.each(fields, function(f) {
						f.resetOriginalValue ? f.resetOriginalValue() : '';
					});
				}, 800);
			}
		});
	},
	focusFirst: function(form){
		var bool = true; 
		if(form.focusFirst == undefined){
			form.focusFirst=true;
		}
		if(!form.readOnly && form.focusFirst){
			Ext.each(form.items.items, function(){
				if(bool && this.hidden == false && this.readOnly == false && this.editable == true){
					this.focus(false, 200);
					bool = false;
				}
			});
		}
	},
	/**
	 * 调整字段显示宽度
	 */
	setItemWidth: function(form, items) {
		var grids = Ext.ComponentQuery.query('gridpanel');
		if(!form.fixedlayout && !form.minMode && 
				form.detailpercent && form.mainpercent && form.detailpercent>0 && form.mainpercent>0 && (form.detailpercent+form.mainpercent)==100){			
			//支持多个grid 调整主从表比例 start hey
			if(grids.length == 1 ){
				form.anchor='100% '+form.mainpercent+'%';
				grids[0].anchor='100% '+form.detailpercent+'%';
			}
			if(grids.length > 1){
				if(grids[0].ownerCt&&(grids[0].ownerCt.xtype=='tabpanel'||grids[0].ownerCt.id=='myTab')){
					form.anchor='100% '+form.mainpercent+'%';
					grids[0].ownerCt.anchor='100% '+form.detailpercent+'%';
				}
			}
			//支持多个grid 调整主从表比例 end hey 
			if(form.ownerCt && form.ownerCt.ownerCt && form.ownerCt.ownerCt.fireResize)form.ownerCt.ownerCt.fireResize();
			if(form.ownerCt && form.ownerCt.fireResize)form.ownerCt.fireResize();
		}
		var formWidth = window.innerWidth, maxSize = 0.097 * formWidth,
			// 宽屏
			wide = screen.width > 1280,
			// 如果该页面只有一个form，而且form字段少于8个
			sm = (!form.fixedlayout && !form.minMode && grids.length == 0 && items.length <= 8),
			// 如果该页面字段过多
			lg = items.length > maxSize;
		Ext.each(items, function(item){
			if(sm) {
				item.columnWidth = 0.5;
			} else if(lg) {
				// 4.0.7版本下必须使用固定宽度
				//新UI暂时不用
				item.width = formWidth*(item.columnWidth);  /*- item.columnWidth*4*10*/
			} else if(form.minMode) {// 布局里面设置为minMode模式
				if(item.columnWidth >= 0 && item.columnWidth < 0.5){
					item.columnWidth = 0.5;
				} else if(item.columnWidth >= 0.5) {
					item.columnWidth = 1;
				}
			} else {
				if(wide) {
					if(item.columnWidth > 0.25 && item.columnWidth < 0.5){
						item.columnWidth = 1/3;
					} else if(item.columnWidth > 0.5 && item.columnWidth < 0.75){
						item.columnWidth = 2/3;
					}
				} else {
					if(item.columnWidth > 0 && item.columnWidth <= 0.25){
						item.columnWidth = 1/3;
					} else if(item.columnWidth > 0.25 && item.columnWidth <= 0.5){
						item.columnWidth = 2/3;
					} else if(item.columnWidth >= 1){
						item.columnWidth = 1;
					}
				}
			}
		});
		if(sm) {
			form.layout = 'column';
		}
	},
	/**
	 * @param necessaryCss 必填项样式
	 */
	setItems: function(form, items, data, limits, necessaryCss){
		var me = this,edit = !form.readOnly,hasData = true,limitArr = new Array();
		if(limits != null && limits.length > 0) {//权限外字段
			limitArr = Ext.Array.pluck(limits, 'lf_field');
		}
		if (data) {
			data = Ext.decode(data);
			if(form.statuscodeField && data[form.statuscodeField] != null && data[form.statuscodeField] != '' &&  
					['ENTERING', 'UNAUDIT', 'UNPOST', 'CANUSE'].indexOf(data[form.statuscodeField]) == -1){//非在录入和已提交均设置为只读// && data[form.statuscodeField] != 'COMMITED'
				form.readOnly = true;
				edit = false;
			}
			if(form.statusCode && data[form.statusCode] == 'POSTED'){//存在单据状态  并且单据状态不等于空 并且 单据状态等于已过账
				form.readOnly = true;
				edit = false;
			}
		} else {
			hasData = false;
		}
		me.setItemWidth(form, items);
		Ext.each(items, function(item){
			if(item.labelAlign&&item.labelAlign!='top'){
				item.labelAlign = 'right';
			}
			if(item.labelAlign=='top'&&item.columnWidth>=1){
				item.margin = '3 0 3 30';
			}
			if(item.group == '0'){
				item.margin = '7 0 0 0';
			}
			//基本背景颜色
			item.labelStyle = "color:#1e1e1e";
			item.fieldStyle = 'background:#fff;color:#313131;';
			if(item.xtype == 'textareafield'){
				item.grow=true;
				item.growMax=300;
			}
			if(!item.allowBlank && item.fieldLabel && necessaryCss.labelColor) {//必填
				/*item.labelStyle = 'color:#' + necessaryCss.labelColor;*/
				item.fieldLabel = "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>"+item.fieldLabel;
				if(item.xtype=='mfilefield'){//附件类型必填设置title颜色
					 this.title = '<font color=#'+necessaryCss.labelColor+'>'+this.title|| '附件'+'</font>' ;
				}
			}
			if(item.readOnly) {
				item.fieldStyle = 'background:#f3f3f3;';
			}
						
			if(item.name != null) {
				if(item.name == form.statusField){//状态加特殊颜色
					item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
				} else if(item.name == form.statuscodeField){//状态码字段强制隐藏
					item.xtype = 'hidden';
				}
			}
			if(item.xtype == 'hidden') {
				item.columnWidth = 0;
				item.width = 0;
				item.margin = '0';
			}
			if(item.xtype == 'checkbox') {
				item.fieldStyle = '';
		        item.margin = '3 0 3 80';
		        if(item.columnWidth<0.25){
					 item.margin = '3 0 3 0';
				}
				item.focusCls = '';
			}
			if(item.maskRe!=null){
				item.maskRe=new RegExp(item.regex);
			}
			if (hasData) {
				item.value = data[item.name];
				if(item.secondname){//针对合并型的字段MultiField
					item.secondvalue = data[item.secondname];
				}
				if(!edit){
					form.readOnly = true;
					item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
					item.readOnly = true;
				} 
				if(item.xtype == 'checkbox'){
					item.checked = Math.abs(item.value || 0) == 1;
					item.fieldStyle = '';
					item.margin = '3 0 3 80';
					if(item.columnWidth<0.25){
						 item.margin = '3 0 3 0';
					}
				}
			}else{
				if(form.source=='allnavigation'){
					item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
					item.readOnly = true;
				}
			}
			if(limitArr.length > 0 && Ext.Array.contains(limitArr, item.name)) {
				item.hidden = true;
			}
			if(item.renderfn){
				var args = new Array();
    			var arr = item.renderfn.split(':');
    			//hey start 主表字段背景颜色
    			if(arr&&arr[0]!='itemstyle'){//判断是否是itemstyle
    				if(contains(item.renderfn, ':', true)){	    		
    					Ext.each(item.renderfn.split(':'), function(a, index){
    						if(index == 0){
    							renderName = a;
    						} else {
    							args.push(a);
    						}
    					});
    				} else {renderName=item.renderfn;}
    				me[renderName](item, args, form);
    			}
    			else{
    		    	switch(arr.length)
    		    	{
    		    		case 2:						
    		    			if(data&&data[item.name]&&data[item.name]==arr[1]) item.fieldStyle = item.fieldStyle + ';background:#c0c0c0;';
    		    			break;
    		    		case 3:	 
    		    			if(data&&data[item.name]&&data[item.name]==arr[1]) item.fieldStyle = item.fieldStyle + ';background:'+arr[2]+';';
    		    			break;
    		    		default:
    		    	}	
    			}
    			//hey end 主表字段背景颜色
			}
		});
		return items;
	},
	setButtons: function(form, buttonString){
		//甘特图使用旧布局
		if(jspName=='plm/task/projectgantttask'){
			form.setButtonsOld(form, buttonString);
			return;
		}
		//四组按钮 按顺序排列 在form--panel.js里定义
		if(buttonString != null && buttonString.trim() != ''){	
			//按钮组集 放入toolbar
			var groups = new Array();
			//标准组
			var base = new Array();			
			//逻辑操作组
			var logic = new Array();			
			//业务组--加入基本按钮 查询、刷新、流程处理、下载数据
			var work = form.enableTools?this.getBaseWork(form):new Array();						
			//关闭组
			var close = new Array();
			//打印按钮组
			var prints = new Array();
			//转单按钮组
			var turns = new Array();
			//更多操作菜单--放入未定义的其它按钮
			var buttons = new Array();
			//辅助功能菜单--加入基本按钮 操作日志、消息日志、发起任务、单据设置
			var help = this.getHelpMenu(form);
			//读取自定义按钮设置
			var data = [];
			if(formCondition!=''){
				var data = this.getButtonGroupSet();
			}
			var closeChange = false;//关闭按钮不在最后
			if(data.length>0){
				var turnButtons=[],baseButtons=[],logicButtons=[],workButtons=[],closeButtons=[];
				Ext.each(data, function(btn){
					if(btn._xtype.indexOf('erpCloseButton')>-1&&btn.groupid!=5){
						closeChange = true;
					}
					switch(btn.groupid)
					{
						case '1':
						  baseButtons.push(btn);
						  break;
						case '2':
						  logicButtons.push(btn);
						  break;
						case '3':
						  if(btn._xtype.indexOf('Print')<0){
						  	 turnButtons.push(btn);
						  }
						  break;
						case '4':
						  workButtons.push(btn);
						  break;
						case '5':
						  closeButtons.push(btn);
						  break;
						default:
						  break;
					}
				});
				if(turnButtons.length>0){
					form.turn_group = Ext.Array.pluck(turnButtons, '_xtype');
				}
				if(baseButtons.length>0){
					form.base_group = Ext.Array.pluck(baseButtons, '_xtype');
				}
				if(logicButtons.length>0){
					form.logic_group = Ext.Array.pluck(logicButtons, '_xtype');
				}
				if(workButtons.length>0){
					form.work_group = Ext.Array.pluck(workButtons, '_xtype');
				}
				if(closeButtons.length>0||closeChange){
					form.close_group = Ext.Array.pluck(closeButtons, '_xtype');
				}
			}
			//分配按钮
			Ext.each(buttonString.split('#'), function(btn, index){
				var o = {};
				var i = 0;
				if(btn.indexOf("erpCallProcedureByConfig")!=-1){
					o.xtype = 'erpCallProcedureByConfig';
					o.name = btn;
				}else if(btn.indexOf('erpCommonqueryButton!')!=-1){
					btn = btn.split('!');
					o.xtype = btn[0];
					o.id = btn[1];
				}else{
					o.xtype = btn;
				}
				//按钮使用自定义的名称  覆盖基本定义的名称
				if(data.length>0){
					Ext.each(data, function(item){
						if(btn.indexOf(item._xtype)>-1){
							o.text = item.text;
							o._detno = item.index;
						}
					});
				}
				o.cls = 'x-btn-gray-right',
				o.height = 26;
				o.width = null;
				o.textAlign = 'center';
				o.style=null;
				o._uiMenu = true;
				if(form.turn_group.indexOf(btn)>-1){
					o.iconCls = 'tbar_turn';
					o.textAlign = 'left';
					turns.push(o);i++;
				}
				if(btn.indexOf('Print')>0){
					o.iconCls = 'x-button-icon-print';
					prints.push(o);i++;
				}
				Ext.each(form.base_group,function(name,index){
					if(btn==name) {base.push(o);i++;}
				});
				Ext.each(form.logic_group,function(name,index){
					if(btn==name) {logic.push(o);i++;}
				});
				Ext.each(form.work_group,function(name,index){
					if(btn==name) {work.push(o);i++;}
				});
				Ext.each(form.close_group,function(name,index){
					if(btn==name) {close.push(o);i++}	
				});
				if(i==0) {
					//批量更新方案特殊界面 加入标准组
					if(jspName=='ma/update/updateScheme'){
						base.push(o);
					}else{
						o.textAlign = 'left';
						buttons.push(o);
					}
				}
			});	
			//按钮排序
			if(data.length>0){
				base.sort(function(a,b){return a._detno-b._detno});		
				logic.sort(function(a,b){return a._detno-b._detno});			
				work.sort(function(a,b){return a._detno-b._detno});					
				turns.sort(function(a,b){return a._detno-b._detno});
			}
			//凭证按钮 --固定分配在逻辑按钮后
			if(form.enableTools){
				logic.push(this.getVoucherButton(form));
			}
			//转单按钮和其它按钮合并					
			if(buttons.length>0||turns.length>0){
				if(buttons.length>0&&turns.length>0){
					Ext.each(buttons,function(button,index){
						turns.push(button);	
				    });
				}else if(buttons.length>0){
					turns = buttons;
				}
				//4个按钮之内不加入更多操作 
				if(turns.length<5){		
				    logic.push(turns);
				}else{
					var _turns = new Array();
					Ext.each(turns,function(button,index){
			    		button.iconCls = 'noicon';
			    		button.cls = 'x-btn-gray';
						_turns.push(button);
			    	});
					logic.push({
						id:'moreOperation',
						height:26,
						text:'更多操作',
						cls: 'x-btn-gray-right',
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
						menu:{
							id:'moreOperation_menu',
							items:_turns,
							listeners:{
								afterrender:function(c){						
									c.ownerCt=form;							
								},
								mouseover: function() {
									this.over = true;
								},
								mouseleave: function() {
									this.over = false;
									this.hide();
								}
							}
						}
					});
				}
			}
			//打印菜单加入业务组 一个打印单独显示 多个打印菜单显示
			if(prints.length>0){
				if(prints.length==1){
					work.push(prints);
				}else{
					var newprints = new Array();
					Ext.each(prints,function(o,index){
						o.iconCls = 'noicon';
						o.textAlign = 'left';
						o.cls = 'x-btn-gray';
						newprints.push(o);
					});
					work.push({
						height:26,
						text:'打印',
						cls: 'x-btn-gray-right',
						menu:{
							id:'print_menu',
							items:newprints,
							listeners: {
								afterrender:function(c){						
									c.ownerCt=form;							
								},
								mouseover: function() {
									this.over = true;
								},
								mouseleave: function() {
									this.over = false;
									this.hide();
								}
							}
						},
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
						}
					});
				}	
			}

			//上一条、下一条 --固定分配在逻辑按钮后 (特殊界面：产品开发任务书)
			if(form.enableTools&&jspName!='plm/task/projectgantttask'){
				work.push(this.getPrevNextButton(form));
			}
			
			//辅助功能菜单加入业务组 (特殊界面：流程设置)
			if(form.enableTools||jspName=='common/jprocessSet'){
				work.push(help);
			}
			
			//基本组
			var basegroup = Ext.create("Ext.ButtonGroup",{
				items:base,
				cls:'x-tbar-group',
				id:'base_group',
				listeners:{
					beforerender:function(c){						
						c.ownerCt=form;						
					},
					afterrender:function(c){						
						c.ownerCt=form;						
					}
				}
			});
			groups.push(basegroup);
			groups.push(' ');

			//逻辑组
			var logicgroup = Ext.create("Ext.ButtonGroup",{
				items:logic,
				cls:'x-tbar-group',
				id:'logic_group',
				listeners:{
					beforerender:function(c){						
						c.ownerCt=form;						
					},
					afterrender:function(c){						
						c.ownerCt=form;						
					}
				}
			});								
			groups.push(logicgroup);
			groups.push(' ');
			//工作组
			var workgroup = Ext.create("Ext.ButtonGroup",{
				items:work,
				cls:'x-tbar-group',
				id:'work_group',
				listeners:{
					beforerender:function(c){						
						c.ownerCt=form;						
					},
					afterrender:function(c){						
						c.ownerCt=form;						
					}
				}
			});							
			groups.push(workgroup);
			groups.push(' ');
			//关闭组
			var closegroup = Ext.create("Ext.ButtonGroup",{
				id:'close_group',
				cls:'x-tbar-group',
				items:close,
				listeners:{
					beforerender:function(c){						
						c.ownerCt=form;						
					},
					afterrender:function(c){						
						c.ownerCt=form;						
					}
				}
			});
			groups.push('->');
			groups.push(closegroup);
			//用于流程界面清空toolbar
			var _Virtual = getUrlParam('_Virtual');
			if(_Virtual&&_Virtual!=-1){
				groups = form.setTools();
			}
			//添加toolbar	
			if(form._toolbar_style){
				//MRP工作台特殊处理
				if(form._toolbar_style=='bottom'){
					var groups_ = new Array();
					Ext.each(groups,function(item,index){
			    		if(index=='0'||index==(groups.length-1)){
			    			groups_.push('->');
			    		}
			    		if(item.id!='work_group'&&item!='->'){
			    			groups_.push(item);
			    		}
			    	});
			    	form.el.dom.style.margin = '0px';
			    	form.el.dom.style.border = '1px solid #e4e4e4';
					form.addDocked({
						xtype: 'toolbar',
						height:50,
						dock: 'bottom',
						items:groups_,
						hidden:_Virtual,
						style:{background:'#e4e4e4',border:'1px solid #e4e4e4'}
					});
				}
			}else{
				form.addDocked({
					xtype: 'toolbar',
					height:36,
					dock: 'top',
					padding:'2 0 4 0',
					id:'form_toolbar',
					items:groups,
					style:_Virtual?'border-bottom:1px solid #e9e9e9;':'border-bottom:1px solid #bdbdbd;'
				});
				form.autoSetBtnStyle(form);
				form.doLayout();
			}
		}else{
			//单独放置辅助功能
			if(form.enableTools){
				//用于流程界面隐藏toolbar
				var _Virtual = getUrlParam('_Virtual');
				_Virtual = _Virtual==1?true:false;
				//辅助功能菜单--加入基本按钮 操作日志、消息日志、发起任务、单据设置
				var help = new Array();
				help.push('->');
				help.push(this.getHelpMenu(form));
				//添加toolbar	
				form.addDocked({
					xtype: 'toolbar',
					height:36,
					dock: 'top',
					margin:'0',
					id:'toolbar',
					items:help,
					hidden:_Virtual
				});
			}
		}
	},
	getBaseWork: function(form){
		var me = form, datalistId = getUrlParam('datalistId'), isRefererList = !!datalistId, 
		hasVoucher = !!me.voucherConfig, dumpable = me.dumpable,
		isNormalPage = !me.dumpable && !me.adminPage, hasList = !me.singlePage;	
		var work = new Array();
		work.push({
			height:26,
			iconCls: 'x-button-icon-query',
			cls:'x-btn-gray-right',
			text: '查询',
			hidden: !isNormalPage,
			listeners:{
				afterrender:function(btn){
					formCondition = getUrlParam('formCondition');
					if(formCondition==null||formCondition==''){
						btn.hide();
					}
				},
				click:function(btn){
					var form = Ext.getCmp('form');
					form.showRelativeQuery();
				}
			}
		},{
			height:26,
			iconCls: 'x-button-icon-refresh',
			cls:'x-btn-gray-right',
			text: '刷新',
			hidden: !isNormalPage,
			listeners:{
				afterrender:function(btn){
					formCondition = getUrlParam('formCondition');
					if(formCondition==null||formCondition==''){
						btn.hide();
					}
				},
				click:function(btn){
					window.location.reload();
				}
			}
		},{
			height:26,
			iconCls: 'x-button-icon-excel',		
			cls:'x-btn-gray-right',
			text: '导出',
			hidden: !isNormalPage,
			listeners:{
				afterrender:function(btn){
					formCondition = getUrlParam('formCondition');
					if(formCondition==null||formCondition==''){
						btn.hide();
					}
				},
				click:function(){
					var form = Ext.getCmp('form');
					var id = Ext.getCmp(form.keyField).value;									
					var grids=Ext.ComponentQuery.query('grid');									
					var obj =new Object();
					if(grids){
						Ext.each(grids,function(g,index){
							if(!g.caller){
								if(g.mainField&&g.mainField!='null')
								obj[caller]=g.mainField;
							
							}else{
								if(g.mainField&&g.mainField!='null')
								obj[g.caller]=g.mainField;
							}
						
						});
					}							
					form.saveAsExcel(id,caller,encodeURI(Ext.JSON.encode(obj)));
			
				}
			}
	    },{
	    	height:26,
	    	iconCls: 'flow',
			text: '流程',
			cls:'x-btn-gray-right',
			hidden: !isNormalPage,
			listeners:{	
				afterrender:function(btn){
					var form = Ext.getCmp('form');
					if(form){
						if(!form.statuscodeField){
							btn.hide(true);
						} else {
							var f = form.statuscodeField;
						if(!Ext.getCmp(f) || Ext.getCmp(f).value == 'ENTERING'){
							btn.hide(true);
						} }
					}	
				},
				click :function(btn){
					var form = Ext.getCmp('form');
					if(!form.statuscodeField){
						btn.hide(true);
					} else {
						var f = form.statuscodeField;
						if(!Ext.getCmp(f) || Ext.getCmp(f).value == 'ENTERING'){
							btn.hide(true);
						} else {
							var id = Ext.getCmp(form.keyField).value;
							if(id != null && id != 0){
								form.getProcess(id);
							}
						}
					}
				}
			}
	    });
		return work;
	},
	getPrevNextButton: function(form){
		var me = form, datalistId = getUrlParam('datalistId'), isRefererList = !!datalistId, 
		hasVoucher = me.voucherConfig?true:me.fo_isPrevNext, dumpable = me.dumpable,
		isNormalPage = !me.dumpable && !me.adminPage, hasList = !me.singlePage;	
		var PrevNextButton = new Array();
		PrevNextButton.push({
			id: 'prev',
			//iconCls: 'x-nbutton-icon-left',
			xtype:'button',
			height:26,
			text: '上一条',
			cls:'x-btn-gray-right',
			hidden : !hasVoucher,
			listeners:{
				render: function(btn){
					if(parent.Ext) {
						var datalist = parent.Ext.getCmp(datalistId);
						if(datalist){
							var datalistStore = datalist.currentStore;
							Ext.each(datalistStore, function(){
								if(this.selected == true){
									if(this.prev == null){
										btn.disable(true);
									}
								}
							});
						} else {
							if(Ext.getCmp(form.codeField)&&Ext.getCmp(form.codeField).value==''){
								btn.hide(true);
							}else{
								btn.disable(true);
							}
						}
					}
				},
				click: function(btn){
					var datalist = parent.Ext.getCmp(datalistId);
					if(datalist){
						var datalistStore = datalist.currentStore;
						var form = Ext.getCmp('form');
						var newId = 0;
						var idx = 0;
						Ext.each(datalistStore, function(s, index){
							if(this.selected == true){
								if(this.prev != null){
									newId = this.prev;
									idx = index;
								}
							}
						});
						datalistStore[idx].selected = false;
						datalistStore[idx-1].selected = true;
						var url = window.location.href;
						if(form.keyField) {
							url = url.replace(/formCondition=(\w*)(IS|=)(\d*)/, 'formCondition=$1$2' + newId);
							url = url.replace(/gridCondition=(\w*)(IS|=)(\d*)/, 'gridCondition=$1$2' + newId);
						}
						window.location.href = url;
					}
				}
			}
		},{
			xtype: 'button',
			//iconCls: 'x-nbutton-icon-right',
			id: 'next',
			text: '下一条',
			cls:'x-btn-gray-right',
			height:26,
			hidden : !hasVoucher,
			listeners:{
				render: function(btn){
					if(parent.Ext) {
						var datalist = parent.Ext.getCmp(datalistId);
						if(datalist){
							var datalistStore = datalist.currentStore;
							Ext.each(datalistStore, function(){
								if(this.selected == true){
									if(this.next == null){
										btn.disable(true);
									}
								}
							});
						} else {
							if(Ext.getCmp(form.codeField)&&Ext.getCmp(form.codeField).value==''){
								btn.hide(true);
							}else{
								btn.disable(true);
							}
						}
					}
				},
				click: function(btn){
					var datalist = parent.Ext.getCmp(datalistId);
					if(datalist){
						var datalistStore = datalist.currentStore;
						var form = Ext.getCmp('form');
						var newId = 0;
						var idx = 0;
						Ext.each(datalistStore, function(s, index){
							if(s.selected == true){
								if(s.next != null){
									newId = s.next;
									idx = index;
								}
							}
						});
						datalistStore[idx].selected = false;
						datalistStore[idx+1].selected = true;
						var url = window.location.href;
						if(form.keyField) {
							url = url.replace(/formCondition=(\w*)(IS|=)(\d*)/, 'formCondition=$1$2' + newId);
							url = url.replace(/gridCondition=(\w*)(IS|=)(\d*)/, 'gridCondition=$1$2' + newId);
						}
						window.location.href = url;
					}
				}
			}
	    });
		return PrevNextButton;
	},
	getVoucherButton: function(form){
		var me = form, datalistId = getUrlParam('datalistId'), isRefererList = !!datalistId, 
		hasVoucher = !!me.voucherConfig, dumpable = me.dumpable,
		isNormalPage = !me.dumpable && !me.adminPage, hasList = !me.singlePage;	
		var VoucherButton = Ext.create("Ext.Button",{
			xtype : 'button',
			text:'凭证',
			height:26,
			cls:'x-btn-gray-right',
			margin:'0 0 0 0',
			id:'Voucher',
			hidden : !hasVoucher,
			listeners : {
				click : function(t) {
					var form = t.ownerCt.ownerCt;
					form.createVoucher(form.voucherConfig);
				},
				render : function(t) {
					if(Ext.getCmp(form.codeField)&&Ext.getCmp(form.codeField).value==''){
						t.hide(true);
					}
				}
			}
		});
		return VoucherButton;
	},
	getHelpMenu: function(form){
		var me = form, datalistId = getUrlParam('datalistId'), isRefererList = !!datalistId, 
		hasVoucher = !!me.voucherConfig, dumpable = me.dumpable,
		isNormalPage = !me.dumpable && !me.adminPage, hasList = !me.singlePage;
		var help = Ext.create("Ext.Button",{
			id:'helpFunction',
			text:'辅助功能',
			height:26,
			cls:'x-btn-gray-right',
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
			menu: {
				listeners: {
					mouseover: function() {
						this.over = true;
					},
					mouseleave: function() {
						this.over = false;
						this.hide();
					}
				},
				items:[{
					  text: '操作日志',	
					  cls:'x-btn-help',
					  listeners:{	
						 afterrender:function(btn){
							 formCondition = getUrlParam('formCondition');
							 if(formCondition==null||formCondition==''){
								 btn.disable();
							 }
						 },
						 click: function(btn){								
							 var form = Ext.getCmp('form');
							 var id = Ext.getCmp(form.keyField).value;
							 if(id != null && id != 0){
								 form.getLogs(id);
							 }
						 }
					  }							
				   },{
					  text: '消息日志',
					  cls:'x-btn-help',
					  listeners:{
					  afterrender:function(btn){
						  formCondition = getUrlParam('formCondition');
						  if(formCondition==null||formCondition==''){
		 					 btn.disable();
						  }
					  },
					  click:function(btn){
						  var form = Ext.getCmp('form');
						  var id = Ext.getCmp(form.keyField).value;
						  if(id != null && id != 0){										
							  me.getMessageInfo(id,caller);}
						  }	
				      }							
				   },{
						cls:'x-btn-help',
						text: '发起任务',
						hidden: !isNormalPage,
						listeners : {
							click : function(btn) {
								var form = Ext.getCmp('form');
								if(!form.codeField){
									btn.disable(true);
								} 
								else form.addTask(form);
							}
						}
					},{
						cls:'x-btn-help',
						text: '导出方案',
						hidden: !dumpable,
						listeners:{
							afterrender:function(btn){
								formCondition = getUrlParam('formCondition');
								if(formCondition==null||formCondition==''){
									btn.disable();
								}
							},
							click:function(){
								// 用于配置方案导出
								me.expData();
							}
						}							
					},{
						cls:'x-btn-help',
						text: '单据设置',
						listeners:{
							afterrender:function(v){
								me.ifadmin(v);							
							},
							click:function(){
								me.reportset();
							}
						}
			}]}
		});
		return help;
	},
	loadNewStore: function(form, param){
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
			url : basePath + "common/loadNewFormStore.action",
			params: param,
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				if(res.data){
					var d = Ext.decode(res.data);
					form.getForm().setValues(d);
					var chs = form.query('checkbox');
					Ext.each(chs, function(){
						this.setValue(Math.abs(d[this.name] || 0) == 1);
					});
					form.getForm().getFields().each(function (item,index,length){
						item.originalValue = item.value;
					});
				}
			}
		});
	},
	getSeqId: function(form){
		if(!form){
			form = Ext.getCmp('form');
		}
		Ext.Ajax.request({
			url : basePath + form.getIdUrl,
			method : 'get',
			async: false,
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success){
					Ext.getCmp(form.keyField).setValue(rs.id);
				}
			}
		});
	},
	/**
	 * 保存之前的判断
	 * @param arg 额外参数
	 */
	beforeSave: function(me, arg){
		var mm = this;
		var form = Ext.getCmp('form');
		if(! mm.checkForm()){
			return;
		}
		if(form.keyField){
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				mm.getSeqId(form);
			}
		}
		var grids = Ext.ComponentQuery.query('gridpanel');


		var removea = new Array();
		Ext.each(grids,function(g,index){
			if(g.xtype=='itemgrid'){
				g.saveValue();
				removea.push(g);
			}
		});

		Ext.each(removea,function(r,index){
			Ext.Array.remove(grids,r);
		});
		if(grids.length > 0 && !grids[0].ignore){
			var param = me.GridUtil.getGridStore();
			if(grids[0].necessaryField&&grids[0].necessaryField.length > 0 && (param == null || param == '')){
				var errInfo = me.GridUtil.getUnFinish(grids[0]);
				if(errInfo.length > 0)
					errInfo = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo+'</div>';
				else
					errInfo = '明细表还未添加数据, 是否继续?';
				warnMsg(errInfo, function(btn){
					if(btn == 'yes'){
						mm.onSave(param, arg);
					} else {
						return;
					}
				});
			} else {
				mm.onSave(param, arg);
			}
		} else {
			mm.onSave([]);
		}
	},
	/**
	 * 单据保存
	 * @param param 传递过来的数据，比如gridpanel的数据
	 */
	onSave: function(param, arg){
		var me = this;
		var form = Ext.getCmp('form');
		param = param == null ? [] : "[" + param.toString() + "]";
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){
					//number类型赋默认值，不然sql无法执行
					if(item.value != null && item.value != ''){

						r[item.name]=item.value;
					}
				}
			});

			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
				if(k == 'msg' && !f && r[k].indexOf('<img')>-1) {//照片字段剔除htmledit
					delete r[k];
				}	
			});
			if(!me.contains(form.saveUrl, '?caller=', true)){
				form.saveUrl = form.saveUrl + "?caller=" + caller;
			}
			me.save(r, param, arg);
		}else{
			me.checkForm();
		}
	},
	save: function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		params.param = unescape(arguments[1].toString());
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			if(arguments[i])
				params['param' + i] = unescape(arguments[i].toString());
		}  
		var me = this;
		var form = Ext.getCmp('form'), url = form.saveUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		}
		me.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					saveSuccess(function(){
						//add成功后刷新页面进入可编辑的页面 
						var value =r[form.keyField];
						var formCondition = form.keyField + "IS" + value ;
						var gridCondition = '';
						var grid = Ext.getCmp('grid');
						if(grid && grid.mainField){
							gridCondition = grid.mainField + "IS" + value;
						}
						if(me.contains(window.location.href, '?', true)){
							window.location.href = window.location.href + '&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						} else {
							window.location.href = window.location.href + '?formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						}
					});
				} else if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
						str = str.replace('AFTERSUCCESS', '');
						saveSuccess(function(){
							//add成功后刷新页面进入可编辑的页面 
							var value = r[form.keyField];
							var formCondition = form.keyField + "IS" + value ;
							var gridCondition = '';
							var grid = Ext.getCmp('grid');
							if(grid && grid.mainField){
								gridCondition = grid.mainField + "IS" + value;
							}
							if(me.contains(window.location.href, '?', true)){
								window.location.href = window.location.href + '&formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							} else {
								window.location.href = window.location.href + '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							}
						});
						showError(str);
					} else {
						showError(str);
						return;
					}
				} else{
					saveFailure();//@i18n/i18n.js
				}
			}

		});
	},
	/**
	 * 检查form未完善的字段
	 */
	checkForm: function(){
		var s = '';
		var form = Ext.getCmp('form');
		form.getForm().getFields().each(function (item, index, length){
			if(!item.isValid()){
				if(s != ''){
					s += ',';
				}
				if(item.fieldLabel || item.ownerCt.fieldLabel){
					s += item.fieldLabel || item.ownerCt.fieldLabel;
				}
			}
		});
		if(s == ''){
			return true;
		}
		showError($I18N.common.form.necessaryInfo1 + '(<font color=green>' + s.replace(/&nbsp;/g,'') + 
				'</font>)' + $I18N.common.form.necessaryInfo2);
		return false;
	},
	/**
	 * 删除操作
	 */
	onDelete: function(id){
		var me = this;
		warnMsg($I18N.common.msg.ask_del_main, function(btn){
			if(btn == 'yes'){
				var form = Ext.getCmp('form');
				if(!me.contains(form.deleteUrl, '?caller=', true)){
					form.deleteUrl = form.deleteUrl + "?caller=" + caller;
				}
				me.setLoading(true);
				Ext.Ajax.request({
					url : basePath + form.deleteUrl,
					params: {
						id: id
					},
					method : 'post',
					callback : function(options,success,response){
						me.setLoading(false);
						var localJson = new Ext.decode(response.responseText);
						if(localJson.exceptionInfo){
							showError(localJson.exceptionInfo);return;
						}
						if(localJson.success){
							delSuccess(function(){
								me.onClose();
							});//@i18n/i18n.js
						} else {
							delFailure();
						}
					}
				});
			}
		});
	},
	/**
	 * 单据修改
	 * @param
	 * @param
	 * @param opts 获取form数据的参数{asString, dirtyOnly, includeEmptyText, useDataValues}
	 * @param extra 额外参数
	 */
	onUpdate: function(me, ignoreWarn, opts, extra){
		var mm = this;
		var form = Ext.getCmp('form');
		var s1 = mm.checkFormDirty(form);
		var s2 = '';
		var grids = Ext.ComponentQuery.query('gridpanel');
		var removea = new Array();
		if(form.codeField && (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
			var code = Ext.getCmp(form.codeField);
			showError(code.fieldLabel+'不能为空!');
			return;
		}
		Ext.each(grids,function(g,index){
			if(g.xtype=='itemgrid'){
				g.saveValue();
				removea.push(g);
			}
		});

		if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
			Ext.each(grids, function(grid, index){
				if(grid.GridUtil){
					var msg = grid.GridUtil.checkGridDirty(grid);
					if(msg.length > 0){
						s2 = s2 + '<br/>' + msg;
					}
				}
			});
		}
		if(s1 == '' && (s2 == '' || s2 == '<br/>')){
			showError('还未添加或修改数据.');
			return;
		}
		Ext.each(removea,function(r,index){
			Ext.Array.remove(grids,r);
		});
		if(form && form.getForm().isValid()){
			//form里面数据
			var r = (opts && opts.dirtyOnly) ? form.getForm().getValues(false, true) : 
				form.getValues();
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){					
					if(item.value != null && item.value != ''){
						r[item.name]=item.value;
					}
				}
			});
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(f && opts && opts.dirtyOnly) {
					extra = (extra || '') + 
					'\n(' + f.fieldLabel + ') old: ' + f.originalValue + ' new: ' + r[k];
				}				
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
				if(k == 'msg' && !f && r[k].indexOf('<img')>-1) {//照片字段剔除htmledit
					delete r[k];
				}
			});
			if(opts && opts.dirtyOnly && form.keyField) {
				r[form.keyField] = form.down("#" + form.keyField).getValue();
			}
			if(!mm.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			var params = [];
			if(grids.length > 0 && grids[0].columns.length > 0 && !grids[0].ignore){
				if(grids[0].GridUtil.isEmpty(grids[0])) {
					warnMsg('明细还未录入数据,是否继续保存?', function(btn){
						if(btn == 'yes' || btn == 'ok'){
							mm.update(r, '[]', extra);
						} else {
							return;
						}
					});
				} else if(grids[0].GridUtil.isDirty(grids[0])) {
					var param = grids[0].GridUtil.getGridStore();
					if(grids[0].necessaryField && grids[0].necessaryField.length > 0 && (param == null || param.length == 0 || param == '') && !ignoreWarn){
						var errInfo = me.GridUtil.getUnFinish(grids[0]);
						if(errInfo.length > 0){
							errInfo = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo+'</div>';
						warnMsg(errInfo, function(btn){
							if(btn == 'yes' || btn == 'ok'){
								params = unescape("[" + param.toString() + "]");
								mm.update(r, params, extra);
							} 
						});
						}else return;																
					} else {
						params = unescape("[" + param.toString() + "]");
						mm.update(r, params, extra);
					}
				} else {
					mm.update(r, '[]', extra);
				}
			} else {
				mm.update(r, params, extra);
			}
		}else{
			mm.checkForm(form);
		}
	},
	update: function(){
		var me = this, params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		params.param = unescape(arguments[1].toString());
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			if (arguments[i] != null)
				params['param' + i] = unescape(arguments[i].toString());
		}
		var form = Ext.getCmp('form'), url = form.updateUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + url,
			params: params,
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showMessage('提示', '保存成功!', 1000);
					//update成功后刷新页面进入可编辑的页面
					var u = String(window.location.href);
					if (u.indexOf('formCondition') == -1) {
						var value = r[form.keyField];
						var formCondition = form.keyField + "IS" + value ;
						var gridCondition = '';
						var grid = Ext.getCmp('grid');
						if(grid && grid.mainField){
							gridCondition = grid.mainField + "IS" + value;
						}
						if(me.contains(window.location.href, '?', true)){
							window.location.href = window.location.href + '&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						} else {
							window.location.href = window.location.href + '?formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						}
					} else {
						window.location.reload();
					}
				} else if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
						str = str.replace('AFTERSUCCESS', '');
						//update成功后刷新页面进入可编辑的页面 
						var u = String(window.location.href);
						if (u.indexOf('formCondition') == -1) {
							var value = r[form.keyField];
							var formCondition = form.keyField + "IS" + value ;
							var gridCondition = '';
							var grid = Ext.getCmp('grid');
							if(grid && grid.mainField){
								gridCondition = grid.mainField + "IS" + value;
							}
							if(me.contains(window.location.href, '?', true)){
								window.location.href = window.location.href + '&formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							} else {
								window.location.href = window.location.href + '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							}
						} else {
							window.location.reload();
						}
					}
					showError(str);return;
				} else {
					updateFailure();
				}
			}
		});
	},
	/**
	 * 关闭之前进行一些判断，根据用户选择或保存或关闭
	 * 
	 */
	beforeClose: function(me){
		var mm = this;
		var s = '';
		var forms = Ext.ComponentQuery.query('erpFormPanel');
		if(forms.length > 0) {
			if(forms[0].readOnly || !forms[0].saveUrl) {
				mm.onClose();
				return;
			}
		}
		if(forms.length > 0 && !forms[0].ignore){//check所有form是否已修改
			Ext.each(forms, function(form, index){
				var msg = mm.checkFormDirty(form);
				if(msg.length > 0){
					s = s + '<br/>' + msg;
				}
			});
		}
		var grids = Ext.ComponentQuery.query('grid');
		if(grids.length > 0 && !grids[0].ignore && me.GridUtil){//check所有grid是否已修改
			Ext.each(grids, function(grid, index){
				var msg = me.GridUtil.checkGridDirty(grid);
				if(msg.length > 0){
					s = s + '<br/>' + msg;
				}
			});
		}
		if(s == '' || s == '<br/>'){
			mm.onClose();
		} else {
			if(!formCondition){//单据新增界面哦
				//关闭前保存新增的数据
				Ext.MessageBox.show({//关闭前保存修改的数据
					title:'保存新添加的数据?',
					msg: '详细:<br/>' + s + '<br/>离开前要保存吗？',
					buttons: Ext.Msg.YESNOCANCEL,
					icon: Ext.Msg.WARNING,
					fn: function(btn){
						if(btn == 'yes'){
							mm.beforeSave(me);
						} else if(btn == 'no'){
							mm.onClose();
						} else {
							return;
						}
					}
				});
			} else {//单据查看界面哦
				Ext.MessageBox.show({
					title:'保存修改?',
					msg: '该单据已被修改:<br/>' + s + '<br/>离开前要保存吗？',
					buttons: Ext.Msg.YESNOCANCEL,
					icon: Ext.Msg.WARNING,
					fn: function(btn){
						if(btn == 'yes'){
							mm.onUpdate(form, []);
						} else if(btn == 'no'){
							mm.onClose();
						} else {
							return;
						}
					}
				});
			}
		}
	},
	/**
	 * 检查表单是否被修改，并返回被修改的内容
	 */
	checkFormDirty: function(form){
		var form = form || Ext.getCmp('form');
		var s = '';
		form.getForm().getFields().each(function (item,index, length){
			if(item.logic!='ignore'){
				var value = item.value == null ? "" : item.value;
				if(item.xtype == 'htmleditor'||item.xtype == 'extkindeditor') {
					value  = item.getValue();
				}
				item.originalValue = item.originalValue == null ? "" : item.originalValue;

				if(Ext.typeOf(item.originalValue) != 'object'){


					if(item.originalValue.toString() != value.toString()){//isDirty、wasDirty、dirty一直都是true，没办法判断，所以直接用item.originalValue,原理是一样的
						var label = item.fieldLabel || item.ownerCt.fieldLabel ||
						item.boxLabel || item.ownerCt.title;//针对fieldContainer、radio、fieldset等
						if(label){
							s = s + '&nbsp;' + label.replace(/&nbsp;/g,'');
						}
					}

				}
			}
		});
		return (s == '') ? s : ('表单字段(<font color=green>'+s+'</font>)已修改');
	},
	/**
	 * 关闭操作
	 */
	onClose: function(){
		var modal=parent.parent.Ext.getCmp('modalwindow')||parent.Ext.getCmp('modalwindow');//层级变动可能导致取不到modalwindow 多加层级判断
		if(modal){
			var history=modal.historyMaster;
			Ext.Ajax.request({
				url: basePath + 'common/changeMaster.action',
				async: false,
				params: {
					to: history
				},
				callback: function(opt, s, r) {
					if (s) {
						modal.close();
					} else {
						alert('切换到原账套失败!');
					}
				}
			});
		}else{
			var main = parent.Ext.getCmp("content-panel")||parent.parent.Ext.getCmp("content-panel")//层级变动可能导致取不到content-panel 多加层级判断
			,bool = false; 
			if(main){
				bool = true;
				main.getActiveTab().close();
			} else {
				var win = parent.Ext.ComponentQuery.query('window');
				if(win){
					Ext.each(win, function(){
						this.close();
					});
				} else {
					bool = true;
					window.close();
				}
			}
			var p = Ext.ComponentQuery.query('erpCloseButton');
			if(!bool && p){//如果还是没关闭tab，直接关闭页面
				window.close();
			}
		}
	},
	/**
	 * 跳转到add页面
	 */
	onAdd: function(panelId, title, url){
		var main = parent.Ext.getCmp("content-panel");
		if(main){
			panelId = (panelId == main.getActiveTab().id || panelId == null) 
			? Math.random() : panelId;
			var panel = Ext.getCmp(panelId); 
			if(!panel){ 
				var value = "";
				if (title.toString().length>5) {
					//修改截取方式
					value = title.toString();	
				} else {
					value = title;
				}
				if(!contains(url, 'http://', true) && !contains(url, basePath, true)){
					url = basePath + url;
				}
				panel = { 
						title : value,
						tag : 'iframe',
						id:'testiframe',
						tabConfig:{tooltip:title},
						border : false,
						layout : 'fit',
						iconCls : 'x-tree-icon-tab-tab',
						html : '<iframe id="iframe_add_'+panelId+'" src="' + url+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
						closable : true
				};
				this.openTab(panel, panelId);
			} else { 
				main.setActiveTab(panel); 
			}
		} else {
			if(!contains(url, basePath, true)){
				url = basePath + url;
			}
			window.open(url);
		}
	}, 
	/**兼容工作台 跳转到add页面
	 * */
	onAddFromBench: function(panelId, title, url){
		var main = parent.Ext.getCmp("content-panel");
		var bool = false;
		if(main){
			bool=true;
		}else if(window.parent.parent.Ext.getCmp("content-panel")){
			main = window.parent.parent.Ext.getCmp("content-panel");
			bool=true;
		}else{
			bool = false;
		}
		if(bool){
			panelId = (panelId == main.getActiveTab().id || panelId == null) 
			? Math.random() : panelId;
			var panel = Ext.getCmp(panelId); 
			if(!panel){ 
				var value = "";
				if (title.toString().length>5) {
					value = title.toString().substring(0,5);	
				} else {
					value = title;
				}
				if(!contains(url, 'http://', true) && !contains(url, basePath, true)){
					url = basePath + url;
				}
				panel = { 
						title : value,
						tag : 'iframe',
						id:'testiframe',
						tabConfig:{tooltip:title},
						border : false,
						layout : 'fit',
						iconCls : 'x-tree-icon-tab-tab',
						html : '<iframe id="iframe_add_'+panelId+'" src="' + url+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
						closable : true
				};
				this.openTab(panel, panelId);
				} else { 
					main.setActiveTab(panel); 
				}
		}else{
			if(!contains(url, basePath, true)){
				url = basePath + url;
			}
			window.open(url);
		}
	},
	/**
	 * 取{field}值,自动赋值给{tField}
	 * @param caller 表名
	 * @param tField 待赋值的字段
	 * @param [record 待赋值的明细行]
	 */
	getFieldValue: function(caller, field, condition, tField, record){
		Ext.Ajax.request({
			url : basePath + 'common/getFieldData.action',
			async: false,
			params: {
				caller: caller,
				field: field,
				condition: condition
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.success){
					if(localJson.data != null){
						if(record){
							record.set(tField, localJson.data);
						} else {
							var ff = Ext.getCmp(tField);
							if(ff)
								ff.setValue(localJson.data);
						}
					}
				}
			}
		});
	},
	/**
	 * 取{fields}值,自动赋值给{tFields},field用','隔开
	 * @param caller 表名
	 * @param tFields 待赋值的字段
	 * @param [record 待赋值的明细行]
	 */
	getFieldsValue: function(caller, fields, condition, tFields, record){
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsData.action',
			async: false,
			params: {
				caller: caller,
				fields: fields,
				condition: condition
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.success){
					if(record){
						if(localJson.data != null){
							var fis = fields.split(',');
							Ext.each(tFields.split(','), function(f, index){
								if( localJson.data[fis[index]] != null){
									record.set(f, localJson.data[fis[index]]);
								}
							});
						}
					} else {
						if(localJson.data != null){
							var fis = fields.split(',');
							Ext.each(tFields.split(','), function(f, index){
								if(localJson.data[fis[index]] != null){
									var fi = Ext.getCmp(f);
									if (fi)
										fi.setValue(localJson.data[fis[index]]);
								}
							});
						}
					}
				}
			}
		});
	},
	/**
	 * 取{fields}值
	 * @param caller 表名
	 */
	getFieldsValues: function(caller, fields, condition, data, fn){
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params: {
				caller: caller,
				fields: fields,
				condition: condition
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.success){
					data = localJson.data;
					fn && fn.call(null, localJson.data);
				}
			}
		});
	},
	/**
	 * 取{field}值
	 * @param caller 表名
	 */
	_getFieldValue: function(caller, field, condition){
		Ext.Ajax.request({
			url : basePath + 'common/getFieldData.action',
			async: false,
			params: {
				caller: caller,
				field: field,
				condition: condition
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return null;
				}
				if(localJson.success){
					if(localJson.data != null){
						return localJson.data;
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		});
	},
	/**
	 * 取{field}值
	 * @param caller 表名
	 */
	_getFieldValues: function(caller, field, condition, tfield, record){
		Ext.Ajax.request({
			url : basePath + 'common/getFieldDatas.action',
			async: false,
			params: {
				caller: caller,
				field: field,
				condition: condition
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return null;
				}
				if(localJson.success){
					if(localJson.data != null){
						if(record){
							record.set(tfield, localJson.data);
						} else {
							Ext.getCmp(tfield).setValue(localJson.data);
						}
					}
				} else {
					return;
				}
			}
		});
	},
	/**
	 * 取{fields}值,field用','隔开
	 * @param caller 表名
	 */
	_getFieldsValue: function(caller, fields, condition){
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsData.action',
			async: false,
			params: {
				caller: caller,
				fields: fields,
				condition: condition
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.success){
					return localJson.data;
				}
			}
		});
	},
	checkFieldValue: function(caller, condition){
		Ext.Ajax.request({
			url : basePath + 'common/checkFieldData.action',
			params: {
				caller: caller,
				condition: condition
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return false;
				}
				if(localJson.success){
					return localJson.data;
				}
			}
		});
	},
	onAudit: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
			if(!me.contains(form.auditUrl, '?caller=', true)){
				form.auditUrl = form.auditUrl + "?caller=" + caller;
			}
			me.setLoading(true);//loading...
			//清除流程
			Ext.Ajax.request({
				url : basePath + me.deleteProcess,
				params: {
					keyValue:id,
					caller:caller,
					_noc:1
				},
				method:'post',
				async:false,
				callback : function(options,success,response){
	
				}
			});
			Ext.Ajax.request({
				url : basePath + form.auditUrl,
				params: {
					id: id
				},
				method : 'post',
				callback : function(options,success,response){
					me.setLoading(false);
					var localJson = new Ext.decode(response.responseText);
					if(localJson.success){
						//audit成功后刷新页面进入可编辑的页面 
						showMessage('提示', '审核成功!', 1000);
						window.location.reload();
					} else {
						if(localJson.exceptionInfo){
							var str = localJson.exceptionInfo;
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace('AFTERSUCCESS', '');
								showMessage("提示", str);
								auditSuccess(function(){
									window.location.reload();
								});
							} else {
								showError(str);return;
							}
						}
					}
				}
			});
		} else {
			me.checkForm();
		}
	},	
	onB2b: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.b2bUrl, '?caller=', true)){
			form.b2bUrl = form.b2bUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		
		Ext.Ajax.request({
			url : basePath + form.b2bUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					//audit成功后刷新页面进入可编辑的页面 
					showMessage('提示', '同步成功!', 1000);
					window.location.reload();
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showMessage("提示", str);
							auditSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onAuditWithManAndTime: function(id,auditerFieldName,auditdateFieldName){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.auditUrl, '?caller=', true)){
			form.auditUrl = form.auditUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		//清除流程
		Ext.Ajax.request({
			url : basePath + me.deleteProcess,
			params: {
				keyValue:id,
				caller:caller,
				_noc:1
			},
			method:'post',
			async:false,
			callback : function(options,success,response){

			}
		});
		Ext.Ajax.request({
			url : basePath + form.auditUrl,
			params: {
				id: id,
				auditerFieldName:auditerFieldName,
				auditdateFieldName:auditdateFieldName
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					//audit成功后刷新页面进入可编辑的页面 
					showMessage('提示', '审核成功!', 1000);
					window.location.reload();
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showMessage("提示", str);
							auditSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onResAuditWithManAndTime: function(id,auditerFieldName,auditdateFieldName){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.resAuditUrl, '?caller=', true)){
			form.resAuditUrl = form.resAuditUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.resAuditUrl,
			params: {
				id: id,
				auditerFieldName:auditerFieldName,
				auditdateFieldName:auditdateFieldName
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				}
				if(localJson.success){
					//resAudit成功后刷新页面进入可编辑的页面 
					showMessage('提示', '反审核成功!', 1000);
					window.location.reload();
				}
			}
		});
	},
	onResAudit: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.resAuditUrl, '?caller=', true)){
			form.resAuditUrl = form.resAuditUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.resAuditUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				}
				if(localJson.success){
					//resAudit成功后刷新页面进入可编辑的页面 
					showMessage('提示', '反审核成功!', 1000);
					window.location.reload();
				}
			}
		});
	},
	onAccounted: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.accountedUrl, '?caller=', true)){
			form.accountedUrl = form.accountedUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.accountedUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					//记账成功后刷新页面进入不可编辑的页面 
					accountedSuccess(function(){
						window.location.reload();
					});
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showMessage("提示", str);
							accountedSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onResAccounted: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.resAccountedUrl, '?caller=', true)){
			form.resAccountedUrl = form.resAccountedUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.resAccountedUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				}
				if(localJson.success){
					//resAudit成功后刷新页面进入可编辑的页面 
					resAccountedSuccess();
					window.location.reload();
				}
			}
		});
	},
	/**
	 * @param allowEmpty 是否允许Grid为空
	 */
	onSubmit: function(id, allowEmpty, errFn, scope, errFnArgs){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
			var s = me.checkFormDirty(form);
			var grids = Ext.ComponentQuery.query('gridpanel');
			if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
				var param = grids[0].GridUtil.getAllGridStore(grids[0]);//获取必填项都填完整的行
				var param2 = grids[0].GridUtil.getGridStore(grids[0]);//获取修改过且必填项都填完整的行
				Ext.each(grids, function(grid, index){//先校验修改过的行，解决render配置formula导致的dirty，实际不会提示要先保存，导致必填项没有填写仍可以提交的问题
					if(grid.GridUtil){
						var msg = grid.GridUtil.checkGridDirty(grid);
						if(msg.length > 0){
							s = s + '<br/>' + grid.GridUtil.checkGridDirty(grid);
						}
					}
				});
				if((s == '' || s == '<br/>') && grids[0].necessaryField && grids[0].necessaryField.length > 0  && (allowEmpty !== true)){
					//①明细行没有修改②明细行有修改，但每行必填项都不完整
					var errInfo = grids[0].GridUtil.getInvalid(grids[0]);//获取grid已保存但部分必填字段没填写的行
					if(errInfo.length > 0)
						{showError("明细表有必填字段未完成填写<hr>" + errInfo);return;}
					else if(param == null || param == '')
						{showError("明细表还未添加数据,无法提交!");return;}					
				}
				
			}
			if(s == '' || s == '<br/>'){
				me.submit(id);
			} else {
				Ext.MessageBox.show({
					title:'保存修改?',
					msg: '该单据已被修改:<br/>' + s + '<br/>提交前要先保存吗？',
					buttons: Ext.Msg.YESNOCANCEL,
					icon: Ext.Msg.WARNING,
					fn: function(btn){
						if(btn == 'yes'){
							if(typeof errFn === 'function')
								errFn.call(scope, errFnArgs);
							else
								me.onUpdate(form, true);
						} else if(btn == 'no'){
							var flag = me.checkOriginalForm(form);
							if(!flag)return;
							if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
								if(grids[0].necessaryField && grids[0].necessaryField.length > 0  && (allowEmpty !== true)){
									//①明细行没有修改②明细行有修改，但每行必填项都不完整
									var errInfo = grids[0].GridUtil.getInvalid(grids[0],true);//获取grid已保存但部分必填字段没填写的行								
									if(errInfo.length > 0){
										showError("明细表有必填字段未完成填写<hr>" + errInfo);return;
									}else if( !(param == null || param == '') && grids[0].store.RawData && grids[0].store.RawData.length == 0){
										showError("明细表添加数据后没有保存,无有效数据，无法提交!");return;
									}else if(param == null || param == ''){
										showError("明细表还未添加数据,无法提交!");return;
									}
								}
							}
							me.submit(id);	
						} else {
							return;
						}
					}
				});
			}
		} else {
			me.checkForm();
		}
	},
//检测修改之前的from值
checkOriginalForm: function(form) {
	var me = this,s='';
	if(form) {
		form.getForm().getFields().each(function (item,index, length){
			if(item.logic!='ignore'){
				var value = item.value == null ? "" : item.value;
				if(item.xtype == 'htmleditor') {
						value  = item.getValue();
					}
				item.originalValue = item.originalValue == null ? "" : item.originalValue;
				if(Ext.typeOf(item.originalValue) != 'object'){
					if(item.originalValue.toString() != value.toString()){//isDirty、wasDirty、dirty一直都是true，没办法判断，所以直接用item.originalValue,原理是一样的
						if(!item.allowBlank) {//不允许为空值
							if(!item.originalValue) {//原值为空
								if(s != ''){
									s += ',';
								}
								if(item.fieldLabel || item.ownerCt.fieldLabel){
									s += item.fieldLabel || item.ownerCt.fieldLabel;
								}
							}
								
						}
					}
				}
			}
		});
		if(s == ''){
			return true;
		}
		showError('必填字段' + '(<font color=green>' + s.replace(/&nbsp;/g,'') + 
				'</font>)' + '未更新,无有效数据,无法提交!');
		return false;
	}
},
	submit: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.submitUrl, '?caller=', true)){
			form.submitUrl = form.submitUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.submitUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					me.getMultiAssigns(id, caller,form);
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							me.getMultiAssigns(id, caller, form,me.showAssignWin);
						} 
						showMessage("提示", str);
					}
				}
			}
		});
	},
	onResSubmit: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.resSubmitUrl, '?caller=', true)){
			form.resSubmitUrl = form.resSubmitUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.resSubmitUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				}
				if(localJson.success){
					//resSubmit成功后刷新页面进入可编辑的页面 
					showMessage('提示', '反提交成功!', 1000);
					window.location.reload();
				}
			}
		});
	},
	onBanned: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.bannedUrl, '?caller=', true)){
			form.bannedUrl = form.bannedUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.bannedUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					//banned成功后刷新页面进入可编辑的页面 
					window.location.href = window.location.href;
					bannedSuccess();
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							bannedSuccess();
							window.location.reload();
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onResBanned: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.resBannedUrl, '?caller=', true)){
			form.resBannedUrl = form.resBannedUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.resBannedUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				}
				if(localJson.success){
					//resBanned成功后刷新页面进入可编辑的页面 
					resBannedSuccess();
					window.location.reload();
				}
			}
		});
	},
	onPrint: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.printUrl && !me.contains(form.printUrl, '?caller=', true)){
			form.printUrl = form.printUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.printUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					window.location.href = window.location.href;
					printSuccess();
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							printSuccess();
							window.location.reload();
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onEnd: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.endUrl && !me.contains(form.endUrl, '?caller=', true)){
			form.endUrl = form.endUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.endUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					endSuccess(function(){
						window.location.reload();
					});
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							endSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onResEnd: function(id, f){
		var me = this;
		var form = f || Ext.getCmp('form');
		if(form.resEndUrl && !me.contains(form.resEndUrl, '?caller=', true)){
			form.resEndUrl = form.resEndUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.resEndUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					resEndSuccess(function(){
						window.location.reload();
					});
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							resEndSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onNullify: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.nullifyUrl && !me.contains(form.nullifyUrl, '?caller=', true)){
			form.nullifyUrl = form.nullifyUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.nullifyUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					nullifySuccess(function(){
						window.location.reload();
					});
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							nullifySuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	batchPrint:function(idS,reportName,condition,title,todate,dateFW,fromdate,enddate,urladdress,whichsystem){
		var printUrl = 'common/BatchPrintController/batchPrint.action';
		var me = this;
		var form = Ext.getCmp('form');
		if(printUrl && !me.contains(printUrl, '?caller=', true)){
			printUrl = printUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...		
		Ext.Ajax.request({
			url : basePath + printUrl,
			params: {
				idS: idS,
				reportName:reportName,
				condition:condition,
				title:title,
				todate:todate,
				dateFW:dateFW,
				fromdate:fromdate,
				enddate:enddate
			},
			method : 'post',
			timeout: 360000,
			callback : function(options,success,response){
				me.setLoading(false);//loading...		
				var res = new Ext.decode(response.responseText);
				//wusy
				if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return;
				}
				var url = urladdress + '?reportfile=' + 
						res.keyData[0]+'&&fdate='+fromdate+'&&tdate='+todate+'&&assifall=have'+'&&asscatecode='+'&&rcondition='+condition+'&&company=&&sysdate='+res.keyData[3]+'&&key='+res.keyData[1]+'&&whichsystem='+whichsystem+'';			
				window.open(url,'_blank');
				/*window.open(url, (form == null ? '' : form.title) + '-批量打印', 'width=' + (window.screen.width-10) + 
						',height=' + (window.screen.height*0.87) + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');*/
			}
		});
	},
	onwindowsPrint2: function(id, reportName, condition, callback){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.printUrl && !me.contains(form.printUrl, '?caller=', true)){
			form.printUrl = form.printUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...	
		Ext.Ajax.request({
			url : basePath + form.printUrl,
			params: {
				id: id,
				reportName:reportName,
				condition:condition   			
			},
			method : 'post',
			timeout: 360000,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo) {
					me.setLoading(false);
					showError(res.exceptionInfo);
					return;
				}				
				 Ext.Ajax.request({
					   url: basePath + 'common/report/print.action',
					   method: 'post',
					   params: {
					      id: id,
						  caller:caller,
						  reportName:reportName,
						  condition:condition   	
					   },
					   callback : function(options, success, response){
						   var res = new Ext.decode(response.responseText);
							me.setLoading(false);
							if(res.success){
								if(res.info.printtype=='jasper'){
									var url= res.info.printUrl+'?userName='+res.info.whichsystem+'&reportName='+res.info.reportname+'&whereCondition='+encodeURIComponent(res.info.condition)+'&printType='+res.info.jasperprinttype;
									window.open(url,'_blank');
								}else if(res.info.isbz=='pdf'){
									window.location.href = res.info.printUrl+'/print?reportname='+res.info.reportname+'&condition='+res.info.condition+'&whichsystem='+res.info.whichsystem+"&"+'defaultCondition='+res.info.defaultCondition;
								}else{
									//var whichsystem = re.whichsystem;
									var url = res.info.printUrl + '?reportfile=' + res.info.reportname + '&&rcondition='+res.info.condition+'&&company=&&sysdate=373FAE331D06E956870163DCB2A96EC7&&key=3D7595A98BFF809D5EEEA9668B47F4A5&&whichsystem='+res.info.whichsystem+'';		
									/*window.open(url, form.title + '-打印', 'width=' + (window.screen.width-10) + 
											',height=' + (window.screen.height*0.87) + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');*/
									window.open(url,'_blank');
								}
							} else if(res.exceptionInfo){
								var str = res.exceptionInfo;
								showError(str);return;
							}
					   }
				   });
			}
		});
	/*	Ext.Ajax.request({
			url : basePath + form.printUrl,
			params: {
				id: id,
				reportName:reportName,
				condition:condition   			
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo) {
					me.setLoading(false);
					showError(res.exceptionInfo);
					return;
				}
				Ext.Ajax.request({
					url : basePath + 'common/enterprise/getprinturl.action',
					params: {
						caller: caller,
						reportName: reportName
					},
					callback: function(opt, s, r) {
						me.setLoading(false);
						var re = Ext.decode(r.responseText);
						if(re.exceptionInfo) {
							showError(re.exceptionInfo);
							return;
						}
						if(re.printurl) {
							
							var whichsystem = re.whichsystem;
							var url = re.printurl + '?reportfile=' + 
							( res.keyData[0]||re.report) + '&&rcondition='+condition+'&&company=&&sysdate='+res.keyData[3]+'&&key='+res.keyData[1]+'&&whichsystem='+whichsystem+'';		
							window.open(url, form.title + '-打印', 'width=' + (window.screen.width-10) + 
									',height=' + (window.screen.height*0.87) + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
							//(re.report || res.keyData[0])
							
							var whichsystem=re.whichsystem;
							var url = re.printurl + '/common/reportviewer.aspx?reportfile=' + 
							res.keyData[0]+'&&rcondition='+condition+'&&company=&&sysdate='+res.keyData[3]+'&&key='+res.keyData[1]+'&&whichsystem='+whichsystem+'';		
							window.open(url, form.title + '-打印', 'width=' + (window.screen.width-10) + 
									',height=' + (window.screen.height*0.87) + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');	
							
						}
						callback && callback.call(null);
					}
				});
			}
		});*/
	},
	onwindowsPrint: function(id, reportName, condition, callback){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.printUrl && !me.contains(form.printUrl, '?caller=', true)){
			form.printUrl = form.printUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...	
		Ext.Ajax.request({
			url : basePath + form.printUrl,
			params: {
				id: id,
				reportName:reportName,
				condition:condition   			
			},
			method : 'post',
			timeout: 360000,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo) {
					me.setLoading(false);
					showError(res.exceptionInfo);
					return;
				}				
				 Ext.Ajax.request({
					   url: basePath + 'common/report/print.action',
					   method: 'post',
					   params: {
					      id: id,
						  caller:caller,
						  reportName:reportName,
						  condition:condition   	
					   },
					   callback : function(options, success, response){
						   var res = new Ext.decode(response.responseText);
							me.setLoading(false);
							if(res.success){
								 if(res.info.printtype=='jasper'){
										var url= res.info.printUrl+'?userName='+res.info.whichsystem+'&reportName='+res.info.reportname+'&whereCondition='+encodeURIComponent(res.info.condition)+'&printType='+res.info.jasperprinttype;
										window.open(url,'_blank');
								 }else if(res.info.printtype=='pdf'){
									window.location.href=res.info.printUrl+'/print?reportname='+res.info.reportname+'&condition='+res.info.condition+'&whichsystem='+res.info.whichsystem+"&"+'defaultCondition='+res.info.defaultCondition;
								}else{
									//var whichsystem = re.whichsystem;
									var url = res.info.printUrl + '?reportfile=' + res.info.reportname + '&&rcondition='+res.info.condition+'&&fdate=&&tdate=&&company=&&sysdate=373FAE331D06E956870163DCB2A96EC7&&key=3D7595A98BFF809D5EEEA9668B47F4A5&&whichsystem='+res.info.whichsystem+'';		
									/*window.open(url, form.title + '-打印', 'width=' + (window.screen.width-10) + 
											',height=' + (window.screen.height*0.87) + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');*/
									window.open(url,'_blank');}
							}else if(res.exceptionInfo){
								var str = res.exceptionInfo;
								showError(str);return;
							}
					   }
					   });
			}
		});
	
	},
	//====================BOM打印方法
	onwindowsPrintBom: function(id, reportName, condition,prodcode){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.printUrl && !me.contains(form.printUrl, '?caller=', true)){
			form.printUrl = form.printUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...		
		Ext.Ajax.request({
			url : basePath + form.printUrl,
			params: {
				id: id,
				reportName:reportName,
				condition:condition,
				prodcode:prodcode
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo) {
					me.setLoading(false);
					showError(res.exceptionInfo);
					return;
				}
				Ext.Ajax.request({
					url : basePath + 'common/enterprise/getprinturl.action',
					params: {
						caller: caller,
						reportName: reportName
					},
					callback: function(opt, s, r) {
						me.setLoading(false);
						var re = Ext.decode(r.responseText);
						if(re.exceptionInfo) {
							showError(re.exceptionInfo);
							return;
						}
						if(re.printurl) {
							var whichsystem=re.whichsystem;
							//var whichsystem = re.whichsystem;
							
							/*var url = re.printurl + '/common/reportviewer.aspx?reportfile=' + 
							(re.report || res.keyData[0]) +'&&rcondition='+condition+'&&company=&&sysdate='+res.keyData[3]+'&&key='+res.keyData[1]+'&&whichsystem='+whichsystem+'';		
							window.open(url, form.title + '-打印', 'width=' + (window.screen.width-10) + 
									',height=' + (window.screen.height*0.87) + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');*/
							var url = re.printurl + '?reportfile=' + 
							(re.report || res.keyData[0]) +'&&rcondition='+encodeURIComponent(condition)+'&&company=&&sysdate='+res.keyData[3]+'&&key='+res.keyData[1]+'&&whichsystem='+whichsystem;		
							/*window.open(url, form.title + '-打印', 'width=' + (window.screen.width-10) + 
									',height=' + (window.screen.height*0.87) + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');*/
							window.open(url,'_blank');
						}
//						window.location.reload();
					}
				});
			}
		});
	},
	//========================
	onPost: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
			var unSave = me.getUnSave(form);
			if(unSave.length > 0) {
				showError('<h3>' + unSave + '</h3> 填写后未保存，请先执行更新操作');
				return;
			}
			var grids = Ext.ComponentQuery.query('gridpanel');
			if(grids.length > 0){
				var invalid = [];
				Ext.each(grids, function(grid, index){
					if(!grid.ignore && grid.GridUtil){
						var msg = grid.GridUtil.getInvalid(grid);
						if(msg!=null && msg.length > 0){
							invalid.push((grids.length > 1 ? ('从表' + (index + 1) + ' ') : '') + msg);
						}
					}
				});
				if(invalid.length > 0) {
					showError('<h3>还有待完善的必填信息：</h3><hr>' + invalid.join('<br>'));
					return;
				}
			}
			if(form.postUrl && !me.contains(form.postUrl, '?caller=', true)){
				form.postUrl = form.postUrl + "?caller=" + caller;
			}
			me.setLoading(true);//loading...
			Ext.Ajax.request({
				url : basePath + form.postUrl,
				params: {
					id: id
				},
				method : 'post',
				callback : function(options,success,response){
					me.setLoading(false);
					var localJson = new Ext.decode(response.responseText);
					if(localJson.success){
						showMessage('提示', '过账成功!', 1000);
						window.location.reload();
					} else {
						if(localJson.exceptionInfo){
							var str = localJson.exceptionInfo;
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace(/AFTERSUCCESS/g, '');
								showError(str);
								window.location.reload();
							} else {
								showError(str);return;
							}
						}
					}
				}
			});
		} else {
			me.checkForm();
		}
	},
	/**
	 * 获取form修改但未保存的字段，并提示
	 */
	getUnSave: function(form) {
		var unSave = [];
		Ext.Array.each(form.items.items, function(item){
			var text = item.fieldLabel || item.boxLabel;
			if(item.firstField)
				item = item.firstField;
			if(typeof item.getValue == 'function' && !item.allowBlank && 
					Ext.isEmpty(item.originalValue) && !Ext.isEmpty(item.value)) {
				text && (unSave.push(text));
			}
		});
		return unSave.join(' ');
	},
	onResPost: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.resPostUrl && !me.contains(form.resPostUrl, '?caller=', true)){
			form.resPostUrl = form.resPostUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.resPostUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showMessage('提示', '反过账成功!', 1000);
					window.location.reload();
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							resPostSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onHung: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.resAuditUrl, '?caller=', true)){
			form.resAuditUrl = form.resAuditUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.hungUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				}
				if(localJson.success){
					//resAudit成功后刷新页面进入可编辑的页面 
					resAuditSuccess();
					window.location.reload();
				}
			}
		});
	},
	onResHung: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.contains(form.resAuditUrl, '?caller=', true)){
			form.resAuditUrl = form.resAuditUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.resHungUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				}
				if(localJson.success){
					//resAudit成功后刷新页面进入可编辑的页面 
					resAuditSuccess();
					window.location.reload();
				}
			}
		});
	},
	onCheck: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.checkUrl && !me.contains(form.checkUrl, '?caller=', true)){
			form.checkUrl = form.checkUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.checkUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					checkSuccess(function(){
						window.location.reload();
					});
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							checkSuccess(function(){
								vwindow.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onConfirm: function(id){
		var me=this;
		var form=Ext.getCmp('form');
		if(form.onConfirmUrl && !me.contains(form.onConfirmUrl, '?caller=', true)){
			form.onConfirmUrl = form.onConfirmUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.onConfirmUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					Ext.Msg.alert('提示', '确认成功', function(){
						window.location.reload();
					});
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							Ext.Msg.alert('提示', '确认成功', function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	onResCheck: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(form.resCheckUrl && !me.contains(form.resCheckUrl, '?caller=', true)){
			form.resCheckUrl = form.resCheckUrl + "?caller=" + caller;
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.resCheckUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					resCheckSuccess(function(){
						window.location.reload();
					});
				} else {
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
							resCheckSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
					}
				}
			}
		});
	},
	openTab : function (panel,id){ 
		var o = (typeof panel == "string" ? panel : id || panel.id); 
		var main = this.getMain(); 
		var tab = main.getComponent(o); 
		if (tab) { 
			main.setActiveTab(tab); 
		} else if(typeof panel!="string"){ 
			panel.id = o; 
			var p = main.add(panel); 
			main.setActiveTab(p); 
		} 
	},
	getMain: function(){
		var main = Ext.getCmp("content-panel");
		if(!main)
			main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		return main;
	},
	getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
		if(!tab && parent.Ext && parent.Ext.getCmp("content-panel"))
			tab = parent.Ext.getCmp("content-panel").getActiveTab();
		if(!tab  && parent.parent.Ext && parent.parent.Ext.getCmp("content-panel"))
			tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
		if(!tab && parent.Ext){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
		return tab;
	},
	/**
	 * string:原始字符串
	 * substr:子字符串
	 * isIgnoreCase:忽略大小写
	 */
	contains: function(string, substr, isIgnoreCase){
		if (string == null || substr == null) return false;
		if (isIgnoreCase === undefined || isIgnoreCase === true) {
			string = string.toLowerCase();
			substr = substr.toLowerCase();
		}
		return string.indexOf(substr) > -1;
	},
	getMultiAssigns:function(id,caller,form){
		Ext.Ajax.request({
			url : basePath + '/common/getMultiNodeAssigns.action',
			params: {
				id: id,
				caller:caller
			},
			method : 'post',
			callback : function(){
				var localJson = new Ext.decode(arguments[2].responseText); 
				if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					showError(str);
				}else {
					if(localJson.MultiAssign){
						if(localJson.autoSetJnode){
							form.SetNodeDealMan(id);
						}else this.showAssignWin(localJson.assigns,id,caller,form);
					}else {
						showMessage('提示', '提交成功!', 1000);
						window.location.reload();
						if(form.onSumitSuccess){
							form.onSumitSuccess();
						}
					}
				}
			},
			scope:this
		});
	},
	showAssignWin :function(persons,id,caller,form){
		var me=this;
		var confirm = new Ext.button.Button({
			text:$I18N.common.button.erpConfirmButton,
			handler:function(btn){
				var panels = Ext.ComponentQuery.query('window >tabpanel>panel');
				var params = new Array(), param = new Object(),flag=0;
				/**调整为必须提交人指定节点处理人，无需默认*/
				Ext.each(panels,function(panel){
					if(panel){
						flag=0;
						Ext.Array.each(panel.items.items,function(item){
							if(item.getValue()){
								param=new Object(),label = item.boxLabel,em_code = label.substring(label.lastIndexOf('(')+1,label.length-1);
								param.em_code = em_code;
								param.nodeId=item.name;
								params.push(JSON.stringify(param));	
								flag=1;
								return false;
							}							
						});
					   if(flag==0) {
						   showError('节点【'+panel.title+'】未指定处理人!');
						   return false;
					   }	
					}
				});
				if(flag==0) return;
				Ext.Ajax.request({
					url: basePath + 'common/takeOverTask.action',
					async: false,
					params: {
						params:unescape(params),
						_noc: 1
					},
					callback: function(options,success,response){
						var text = response.responseText;
						jsonData = Ext.decode(text);
						//再发送请求 
						if(jsonData.success){							
							//如果流程处理人选择自己，则跳过，选择下一节点处理人
							me.getMultiAssigns(id, caller, form);								

							win.close();
							Ext.Msg.alert('提示' ,"指派成功!");
						}else{
							Ext.Msg.alert('提示' ,"指派失败!");
							win.close();
						}
						//window.location.reload();
					}
				});
			}
		});
		var cancel = new Ext.button.Button({
			text:$I18N.common.button.erpCancelButton,	
			handler:function(){
				win.close();
				window.location.reload();
			}
		});
		var searchKey = new Object();
		var win = Ext.create('Ext.window.Window', {
			title:'<div align="center">节点处理人</div>',
			height: 450,
			width: 650,
			layout:'border',
			closable:false,
			modal:true,
			id:'win',
			autoScroll:true,
			buttonAlign:'center',
			buttons: [confirm,cancel],
			items: []    	   
		});
		win.add([{
			xtype:'textfield',
			margin:'10 20 10 20',
			fieldLabel:'快速搜索',
			labelStyle:'font-weight:bold;',
			id:'searchtextfield',
			//columnWidth:0.8,		 
			//flex:1,
			enableKeyEvents:true,
			region:'north',
			listeners:{
				keydown:function(field,e){
					if(e.getKey()==Ext.EventObject.ENTER){	
						searchKey[Ext.getCmp('processTab').getActiveTab().id]=field.value;
						var results=Ext.Array.filter(persons[Ext.getCmp('processTab').getActiveTab().id].JP_CANDIDATES,function(JP_CANDIDATE){
							if(field.value==undefined || JP_CANDIDATE.indexOf(field.value)!=-1) return JP_CANDIDATE;
						});
						Ext.Array.each(Ext.getCmp('processTab').getActiveTab().personUsers,function(item){
							Ext.getCmp('processTab').getActiveTab().remove(item);
						});						
						me.addUserItems(Ext.getCmp('processTab').getActiveTab(),persons[Ext.getCmp('processTab').getActiveTab().id].JP_NODEID,results);

					}
				}
			}
		}]);
		this.addAssignItems(win,persons,searchKey);
		win.show();
	},
	addAssignItems:function(win,persons,searchKey){
		var me=this;
		var tab = new Ext.TabPanel({
				   id : 'processTab',
				   region:'center',
				   enableTabScroll : true,
				   closeAll : true,
				   minTabWidth :80,
				   autoHeight:true,  
				   resizeTabs : true,
				   listeners:{
					   'tabchange':function(tabPanel,newCard,oldCard,eOpts){
						   Ext.getCmp('searchtextfield').setValue(searchKey[newCard.id]);
					   		}
					   }
				 });
		win.add(tab);	
		for (var i = 0; i < persons.length; i++){
			 var panel=new Ext.Panel({
		           id:i.toString(),
		           autoHeight:true,  
		           autoScroll:true,
		           layout:'column',
		           bodyStyle: 'background:#e0e0e0',
		           title:persons[i].JP_NODENAME
		          });
			 tab.add(panel);
			 me.addUserItems(panel,persons[i].JP_NODEID,persons[i].JP_CANDIDATES);
			}
		tab.setActiveTab(0);
	},	 
	addUserItems:function(panel,jp_nodeid,jp_candidates){
		var me=this;
		var maxSize=jp_candidates.length>24?24:jp_candidates.length,personUsers=new Array(),user=null,more=Ext.getCmp('more'+panel.id);		
		if(more)more.destroy();
		for(var j=0;j<maxSize;j++){
				user=Ext.create('Ext.form.field.Radio',{
					name:jp_nodeid,
					boxLabel:jp_candidates[j],
					columnWidth: 0.33,
					fieldCls:'x-myradio'
					//checked: j==0?true:false
				});
				personUsers.push(user);			
			}			 
		panel.add(personUsers);
		panel.personUsers=personUsers;
		if(jp_candidates.length>maxSize){
			panel.add({ xtype: 'textfield',
				readOnly:true,
				labelSeparator:'',
				columnWidth:1,
				id:'more'+panel.id,
				fieldStyle : 'background:#e0e0e0;border-bottom:none;vertical-align:middle;border-top:none;border-right:none;border-bottom:none;border-left:none;',
				fieldLabel: '『<a href="#" class="terms">全部</a>』',
				listeners: {
					click: {
						element: 'labelEl',
						fn: function(e,el) {
							var target = e.getTarget('.terms');
                            Ext.getCmp('more'+panel.id).destroy();
							if (target) {
								Ext.Array.each(panel.personUsers,function(item){
									panel.remove(item);
								});	
								var personUsers=new Array();
								for(var i=0;i<jp_candidates.length;i++){
									user=Ext.create('Ext.form.field.Radio',{
										name:jp_nodeid,
										boxLabel:jp_candidates[i],
										columnWidth: 0.33,
										fieldCls:'x-myradio',
										checked: i==0?true:false
									});
									personUsers.push(user);			
								}
								panel.add(personUsers);
								panel.personUsers=personUsers;
								e.preventDefault();
							}
						}
					}
				}
			});	
		}			
	},
	setLoading : function(b) {// 原this.getActiveTab().setLoading()换成此方法,解决Window模式下无loading问题
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
	},
	link: function(item,args){
		if(item.xtype=='multifield'){
			item.listeners={
					afterrender:function(item){
						var f=item.firstField;
						if(f.value){
							f.setFieldStyle({ 'color': 'blue'});
							f.focusCls = 'mail-attach';	
							var index = 0,url=args[0], length = url.length, s, e;
							while(index < length) {
								if((s = url.indexOf('{', index)) != -1 && (e = url.indexOf('}', s + 1)) != -1) {
									url = url.substring(0, s) + Ext.getCmp(url.substring(s+1, e)).value + url.substring(e+1);
									index = e + 1;
								} else {
									break;
								}
							}
							f.inputEl.addListener('click',function(evt,el){
								openUrl(url);
							});
						}
					}
			};
		}else {
			   item.fieldStyle=item.fieldStyle?item.fieldStyle+';color:blue':'color:blue';
			   item.focusCls = 'mail-attach';	
			   item.listeners={
						click: {
							element:'inputEl',
							buffer : 100,
							fn: function(e,el) {
	                            if(item.value){
	                            	var index = 0,url=args[0], length = url.length, s, e;
	    							while(index < length) {
	    								if((s = url.indexOf('{', index)) != -1 && (e = url.indexOf('}', s + 1)) != -1) {
	    									url = url.substring(0, s) + Ext.getCmp(url.substring(s+1, e)).value + url.substring(e+1);
	    									index = e + 1;
	    								} else {
	    									break;
	    								}
	    							}
	    						   openUrl(url);
	                            }
							}	  
						}
				}; 
		}	
	},
	Highlight:function(item,args){
		var style='font-weight:bold;color:';
		style+=(args && args[0])?args[0]+";":'red;'; 
		item.fieldStyle=item.fieldStyle?item.fieldStyle+style:style;
	},
	formula: function(item, args, form){
		Ext.defer(function(){
			var mm = this, field = form.down('#' + item.name), vals = {};
			if(field != null && !form.readOnly) {
				field.addEvents({'formula': true});
				field.addListener('formula', function(f, changedField, changedFieldVal){
					if(changedField) {
						vals[changedField] = changedFieldVal || 0;						
					}
					// eg: eval("var a = 1, b = 2, c = 3;a + b / c")
					var exp = 'var ' + Ext.Object.toQueryString(vals).replace(/&/gi, ",") + ";" + args[0];
					field.setValue(eval(exp) || 0);
				});
				var fields = args[0].split(/\+|-|\*|\(|\)|\//g);
				Ext.Array.each(fields, function(f){
					if(f) {
						var ff = form.down('#' + f);
						if(ff) {
							vals[f] = ff.getValue() || 0;
							ff.on('change', function(_f, newVal){
								field.fireEvent('formula', field, _f.getName(), _f.getValue());
							});
						}
					}
				});
				field.fireEvent('formula');
			}
		}, 500);
	},
	autoDbfind: function(caller, field, condition) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'common/getOrderChange.action',
			params: {
				which: 'form',
				caller: caller,
				field: field,
				condition: condition,
				_config:getUrlParam('_config')
			},
			async: false,
			method: 'post',
			callback: function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				if (res.data) {
					var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					if(data.length<1){
						showError(getUrlParam('operation')+'，勾选明细不符合操作条件！');
						me.onClose(me);
					}
					me.autoSetValue(data[0], res.dbfinds, field);
				}
			}
		});
	},
	autoSetValue: function(data, dbfinds, field) {
		var me = this;
		Ext.Array.each(Ext.Object.getKeys(data),function(k) {
			Ext.Array.each(dbfinds,function(ds) {
				if (k == ds.dbGridField) {
					var ff = Ext.getCmp(ds.field);
					if (ff && ff.setValue) ff.setValue(data[k]);
				}
			});
		});
		var trigger = Ext.getCmp(field);
		if(trigger){
			var record = new Object();
			record.data = data;
			trigger.fireEvent('aftertrigger', trigger, record,dbfinds);
		}
	},
	addItemsForUI:function(form,items,single){
		//分组设置
		var baseitems = new Array();//基本资料
		var panelitems = new Array();//分组items
		var groupname;//分组名称
		var groupcode = 0;//分组编号
		var panels = new Array();//选项卡	
		var actNum = 0;//实际展示字段
		var basegroupname;//基本组名称
		Ext.each(items, function(item, index){
			if(item.xtype!='hidden'){
				actNum++;
			}
		});
		if(jspName=='sys/Feedback'){
			actNum = 0;
		}
		//读取系统设置 是否开启折叠显示
		var openGroupStyle = false;
		Ext.Ajax.request({
    		url: basePath + 'ma/setting/config.action?caller=sys&code=changeGroupStyle',
    		method: 'GET',
    		async: false,
    		callback: function(opt, s, r) {
    			if(r && r.status == 200) {
    				if(r.responseText){
    					var res = Ext.JSON.decode(r.responseText);
	    				if(res.data==1){
	    					openGroupStyle = true;
	    				}
    				}
    			}
    		}
    	});
		//读取逻辑设置 特殊界面需要将分组全部折叠
    	var foldAllPanel = false;
    	if(openGroupStyle){
    		Ext.Ajax.request({
	    		url: basePath + 'ma/setting/config.action?caller='+caller+'&code=foldAllPanel',
	    		method: 'GET',
	    		async: false,
	    		callback: function(opt, s, r) {
	    			if(r && r.status == 200) {
	    				if(r.responseText){
	    					var res = Ext.JSON.decode(r.responseText);
		    				if(res.data==1){
		    					foldAllPanel = true;
		    				}
	    				}
	    			}
	    		}
	    	});
    	}
		Ext.each(items, function(item, index){
			//是否启动页签布局
			if(!openGroupStyle){
				form.add(items);
				return false;
			}
			//字段小于60个的单表 按照原布局分组
			if(actNum<60&&single){
				form.add(items);
				return false;
			}						
			//无分组直接添加
			if(item.group==1&&groupcode==0){
				form.add(items);
				return false;
			}
			//分组信息获取并添加到tabpanel
			if(item.group==0){
				if(groupcode==0){
					basegroupname=item.title;
				}
				groupcode++;
				if(groupcode==2){
					if(!foldAllPanel){
						form.add(baseitems);
					}else{//全部折叠布局时第一组也加入tabpanel
						panels.push({
							xtype:'panel',
							title:basegroupname,
							layout : 'column',
							items:baseitems
						});
					}
					groupname=item.title;
				}
				//从第二次分组开始添加panel
				if(groupcode>2){
					panels.push({
						xtype:'panel',
						title:groupname,
						layout : 'column',
						items:panelitems
					});	
					groupname=item.title;
					panelitems = new Array();										
				}
			}					
			//第一分组设为基本信息
			if(item.group==1&&groupcode==1){
				baseitems.push(item);
			}
			//之后分组加入tabpanel
			if(item.group!=0&&groupcode!=1){
				panelitems.push(item);
			}
			//最后一组为form添加tabpanel
			if(items.length-index==1){
				panels.push({
					xtype:'panel',
					title:groupname,
					layout : 'column',
					items:panelitems
				});		
			}
		});
		if(panels.length>0){
			var tabpanel = Ext.create("Ext.TabPanel",{
				id:'newStyle_Tab',
				columnWidth:1,
				enableTabScroll:true,
				overflowX : 'hidden',
				items:panels
			});
			tabpanel.setActiveTab(0);
			form.add(tabpanel);
			form.doLayout();
		}
	},
	getButtonGroupSet:function(){
    	var data = [];
    	Ext.Ajax.request({
			method: 'post',
            url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'BGS_DETNO,BGS_XTYPE,BGS_NAME,BGS_GROUP,BGS_CALLER,BGS_GROUPID',
				caller : 'BUTTONGROUPSET',
				condition : 'BGS_CALLER = \''+ caller +'\''
			},
            callback: function(options, success, response) {
            	var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				Ext.Array.each(Ext.decode(rs.data), function(item){
					data.push({
						index:item.BGS_DETNO,
						_xtype:item.BGS_XTYPE,
						text:item.BGS_NAME,
						groupname:item.BGS_GROUP,
						groupid:item.BGS_GROUPID
					});
				});
            }
		});
		return data;
    }
});