/**
 *采购询价单：查看历史报价
 */	
Ext.define('erp.view.core.button.HistoryQuotation',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpHistoryQuoButton',
		iconCls: 'x-button-icon-yuan',
    	cls: 'x-btn-gray',
    	id: 'historyquo',
    	text: $I18N.common.button.erpHistoryQuoButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
			this.addEvents({
				base: true,
				formal: true
			});
		},
		getWin : function(prod,flag){
			var win = Ext.getCmp('history-win');
			if(win == null){
				win = Ext.create('Ext.window.Window', {
					id: 'history-win',
					width: '80%',
					height: '100%',
					maximizable : true,
					layout: 'anchor',
					closeAction: 'hide',
					setMyTitle: function(code){//@param code 料号
						if(!flag){
							this.setTitle('编号:<font color=blue>' + code + '</font>&nbsp;的报价历史&nbsp;&nbsp;' + 
	    							'<input type="button" value="上一条" onClick="javascript:Ext.getCmp(\'history-win\').prev();" style="cursor: pointer;color:gray;font-size:13px;"/>' + 
	    							'<input type="button" value="下一条" onClick="javascript:Ext.getCmp(\'history-win\').next();" style="cursor: pointer;color:gray;font-size:13px;"/>');
						}else{
							this.setTitle('编号:<font color=blue>' + code + '</font>&nbsp;的报价历史&nbsp;&nbsp;');
						}
					},
					reload: function(code){//@param code 料号
						var g = this.down('grid[id=history]');
						g.GridUtil.loadNewStore(g, {
							caller: g.caller,
							condition: "id_prodcode='" + code + "' and in_status='已审核' and in_checkstatus='已批准' "
						});
						g = this.down('grid[id=invid]');
						g.GridUtil.loadNewStore(g, {
							caller: g.caller,
							condition: "ppd_prodcode='" + code + "' AND ppd_statuscode='VALID'"
						});
						this.setMyTitle(code);
					},
					prev: function(){//查看上一条
						var item = Ext.getCmp('grid').prev();
						if(item != null){
							this.reload(item.data['id_prodcode']);
						}
					},
					next: function(){//查看下一条
						var item = Ext.getCmp('grid').next();
						if(item != null){
							this.reload(item.data['id_prodcode']);
						}
					}
				});
				win.setMyTitle(prod);
				win.show();
				win.add(Ext.create('erp.view.core.grid.Panel2', {
					id: 'history',
					anchor: '100% 60%',
					caller: 'Inquiry!History',
					condition: "id_prodcode='" + prod + "' and in_status='已审核' and in_checkstatus='已批准' ",
					bbar: null,
					listeners: {
						reconfigure: function(){
        					win.add(Ext.create('erp.view.core.grid.Panel2', {
    							id: 'invid',
    							title: '现有效价格',
    							anchor: '100% 40%',
    							caller: 'PurchasePrice!Invid',
    							condition: "ppd_prodcode='" + prod + "' AND ppd_statuscode='VALID'",
    							bbar: null
    						}));
						}
					}
				}));
			} else {
				win.reload(prod);
				win.show();
			}
		}
	});