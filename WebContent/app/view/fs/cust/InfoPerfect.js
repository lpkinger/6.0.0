Ext.define('erp.view.fs.cust.InfoPerfect',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype:'tabpanel',
				id:'infoperfect',
				title : '项目风控报告',
				items: [{ 
					title : '基本情况',
					tag : 'iframe',
					id:'base',
					tabConfig:{tooltip:'基本情况'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/cust/custSurveyBase.jsp?formCondition=cq_idIS'+caid+'&gridCondition=mf_cqidIS'+caid+'&readOnly='+readOnly+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '经营场所情况',
					tag : 'iframe',
					id:'manage',
					tabConfig:{tooltip:'经营场所情况'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_manage" src="'+basePath+'jsps/fs/cust/businessCondition.jsp?formCondition=bc_idIS'+caid+'&gridCondition=pm_bcidIS'+caid+'&readOnly='+readOnly+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '资信情况',
					tag : 'iframe',
					id:'analysis',
					tabConfig:{tooltip:'资信情况'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_analysis" src="'+basePath+'jsps/fs/cust/custCreditStatus.jsp?formCondition=cc_caidIS'+caid+'&gridCondition=cb_caidIS'+caid+'&readOnly='+readOnly+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '财务情况',
					tag : 'iframe',
					id:'financcondition',
					tabConfig:{tooltip:'财务情况'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_fareportanalysis" src="'+basePath+'jsps/fs/cust/financCondition.jsp?formCondition=fc_caidIS'+caid+'&gridCondition=ai_alidIS'+caid+'&readOnly='+readOnly+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}]
			}]
		}); 
		this.callParent(arguments); 
	}
});