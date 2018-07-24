Ext.define('erp.view.fs.cust.HXInfoPerfect',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype:'tabpanel',
				id:'hxinfoperfect',
				title : '买方额度调查报告',
				items: [{ 
					title : '基本情况',
					tag : 'iframe',
					id:'base',
					tabConfig:{tooltip:'基本情况'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/cust/hxCustSurveyBase.jsp?formCondition=sb_caidIS'+caid+'&gridCondition=fis_caidIS'+caid+'&readOnly='+readOnly+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '经营情况',
					tag : 'iframe',
					id:'manage',
					tabConfig:{tooltip:'经营情况'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_manage" src="'+basePath+'jsps/fs/cust/hxBusinessCondition.jsp?formCondition=bc_caidIS'+caid+'&readOnly='+readOnly+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '财务情况',
					tag : 'iframe',
					id:'analysis',
					tabConfig:{tooltip:'财务情况'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_analysis" src="'+basePath+'jsps/fs/cust/hxFinancCondition.jsp?formCondition=fc_caidIS'+caid+'&gridCondition=fi_caidIS'+caid+'&readOnly='+readOnly+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}]
			}]
		}); 
		this.callParent(arguments); 
	}
});