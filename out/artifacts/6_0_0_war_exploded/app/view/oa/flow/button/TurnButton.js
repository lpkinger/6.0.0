/*
 * 用于按钮点击后跳转到第一个active界面
 * 初始化保存按钮 参数控制判断是 派生按钮还是普通保存按钮
 */
Ext.define('erp.view.oa.flow.button.TurnButton',{ 
		extend: 'Ext.Button', 
		alias: 'widget.TurnButton',
    	cls: 'x-btn-gray',
    	height:22,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(btn){
			var tab = btn.ownerCt.ownerCt.ownerCt;
			//判断派生流程目标是否存在
			var count;
			if(btn.type=='Flow'){
				Ext.Ajax.request({
					url : basePath + 'common/getFieldsDatas.action',
					async: false,
					params:{
						fields : 'nvl(count(1),0) as v_count',
						caller : 'flow_define left join flow_operation on fo_flowcaller=fd_caller and fo_flowname=fd_shortname',
						condition : "fo_fdshortname='"+btn.version+"'"
					},
					callback : function(options,success,response){
						var rs = new Ext.decode(response.responseText);
						if(rs.exceptionInfo){
							showError(rs.exceptionInfo);return;
						}
					    if(rs.success){
					    	var data = new Ext.decode(rs.data);
					    	count=data[0].V_COUNT;
					    	
					    }
					}
				});
				if(count==0){
					Ext.MessageBox.alert("消息","派生流程已经删除，不允许派生流程！");
					return false;
				}
			}
			//生成新tab 点击后的panel界面固定加载保存和关闭
			tab.insert(0,{
				_foid : btn.foid,
			    _foname: btn.foname,
				_btnType:btn.type,//来源按钮类型 1.保存 2.派生任务 3.派生流程
				_edit:true,//是否编辑
				reorderable:false,//不可拖动
				_group:btn.group,
				xtype:'FlowPanel',
				title:btn.text,
				tbar:{cls:'x-flow-tbar',padding: '4 5 0 5',items:[{
					xtype:'SaveButton',
					_id: btn.foid,
					height:22,
					_type: btn.type
				},{
					height:22,
					xtype:'button',
					cls:'x-btn-gray',
					text:'关闭',
					handler:function(btn){
						//显示tbar
						var tab = btn.ownerCt.ownerCt.ownerCt;
						var panels = tab.items.items;
						Ext.each(panels, function(panel){
							if(panel._first){
								if(panel.dockedItems&&panel.dockedItems.items){
									panel.dockedItems.items[0].show();
								}
							}
						});
						var acTab = tab.getActiveTab();
						tab.remove(acTab);
						tab.doLayout();
						tab.setActiveTab(0);
					}
				}]}
			});
			//隐藏tbar
			var panels = tab.items.items;
			Ext.each(panels, function(panel){
				if(panel._first){
					if(panel.dockedItems&&panel.dockedItems.items){
						panel.dockedItems.items[0].hide();
					}
				}
			});
			tab.setActiveTab(0);
		}
	});