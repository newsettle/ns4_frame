package com.creditease.framework.util;


/**
 *
 * @author Frank E-mail:chudaping＠hotmail.com
 * @version 创建时间：Mar 8, 2014 9:33:59 PM
 * 
 */
public class ConvertBOMFile {
	private String files= "/src/com/los/chat/Channel.java,/src/com/los/chat/ChatContent.java,/src/com/los/chat/ChatManager.java,/src/com/los/engineAction/CheckBuff.java,/src/com/los/engineAction/CheckSkill.java,/src/com/los/engineAction/DelBuff.java,/src/com/los/engineAction/OccupyBattleEnd.java,/src/com/los/engineAction/OccupyBattleInit.java,/src/com/los/engineAction/SkillEnd.java,/src/com/los/engineAction/StrengthenEnd.java,/src/com/los/engineAction/TradeEnd.java,/src/com/los/generator/CityMonsterGenerator.java,/src/com/los/generator/CopySceneGenerator.java,/src/com/los/generator/EquipmentGenerator.java,/src/com/los/generator/ExpData.java,/src/com/los/generator/GameEntityGenerator.java,/src/com/los/generator/ItemCreateGen.java,/src/com/los/generator/MessageGenerator.java,/src/com/los/generator/TestCopySceneRole.java,/src/com/los/generatortask/AllianceTaskCondition.java,/src/com/los/generatortask/AmountEventTaskCondition.java,/src/com/los/generatortask/BuildingCountTaskCondition.java,/src/com/los/generatortask/BuildingTaskCondition.java,/src/com/los/generatortask/CondCountTaskCondition.java,/src/com/los/generatortask/CountTaskCondition.java,/src/com/los/generatortask/CurrentCityPropTaskCondition.java,/src/com/los/generatortask/EquipCountTaskCondition.java,/src/com/los/generatortask/Generator.java,/src/com/los/generatortask/OwnedTaskCondition.java,/src/com/los/generatortask/OwningTaskCondition.java,/src/com/los/generatortask/Soldier.java,/src/com/los/generatortask/TaskCondition.java,/src/com/los/generatortask/TaskWrapper.java,/src/com/los/generatortask/TechTaskCondition.java,/src/com/los/generatortask/UserColTaskCondition.java,/src/com/los/generatortask/UserEventTaskCondition.java,/src/com/los/handler/AllianceHandler.java,/src/com/los/handler/ButtonHandler.java,/src/com/los/handler/FaithHandler.java,/src/com/los/handler/ItemEquipHandler.java,/src/com/los/handler/OccBattleHandler.java,/src/com/los/handler/RankHandler.java,/src/com/los/handler/SceneHandler.java,/src/com/los/handler/SkillHandler.java,/src/com/los/handler/SlotHandler.java,/src/com/los/handler/TaskHandler.java,/src/com/los/handler/TeamHandler.java,/src/com/los/handler/TradeHandler.java,/src/com/los/model/EquipmentBag.java,/src/com/los/model/Task.java,/src/com/los/model/User.java,/src/com/los/net/ActionHandler.java,/src/com/los/net/Client.java,/src/com/los/net/ClientConnection.java,/src/com/los/net/SecurityXMLServer.java,/src/com/los/path/MapData.java,/src/com/los/path/Point.java,/src/com/los/path/Road.java,/src/com/los/path/Sign.java,/src/com/los/quad/QuadNodeItem.java,/src/com/los/quad/QuadTree.java,/src/com/los/quad/QuadTreeNode.java,/src/com/los/role/Charactor.java,/src/com/los/role/CityMonster.java,/src/com/los/role/ItemPrize.java,/src/com/los/role/Monster.java,/src/com/los/role/MonsterUser.java,/src/com/los/role/NPC.java,/src/com/los/role/Player.java,/src/com/los/role/PointRouter.java,/src/com/los/role/RandomRouter.java,/src/com/los/role/Role.java,/src/com/los/role/Router.java,/src/com/los/role/Skillutil.java,/src/com/los/role/SlotPrize.java,/src/com/los/role/User.java,/src/com/los/role/VerticalRouter.java,/src/com/los/scene/CopyScene.java,/src/com/los/scene/MetaSceneData.java,/src/com/los/scene/Scene.java,/src/com/los/server/ServerManager.java,/src/com/los/skill/Buff.java,/src/com/los/skill/BuffPool.java,/src/com/los/skill/FightCalc.java,/src/com/los/skill/MetaItemSkill.java,/src/com/los/skill/MetaSkill.java,/src/com/los/skill/Skill.java,/src/com/los/util/DataConfig.java,/src/com/los/util/DateUtil.java,/src/com/los/util/Tools.java";

	public void convert() {
		String ff[] = files.split(",");
		int succ = 0;
		for(String f : ff) {
			try {
				succ += trimBOMFile(f)?1:0;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("[完成全部convert] ["+succ+"]");
	}
	
	public static boolean trimBOMFile(String file) throws Exception {
		String content = FileUtils.readFile(file);
		byte data[] = content.getBytes();
		boolean bom = isBOMFormat(data);
		if(bom) {
			byte nd[] = new byte[data.length-3];
			System.arraycopy(data, 3, nd, 0, nd.length);
			FileUtils.writeFile(file, nd);
		}
		if(bom) {
			System.out.println("[是BOM类型] ["+file+"]");
		}
		return bom;
	}
	
	public static String trimBOM(String content) {
		byte data[] = content.getBytes();
		byte nd[] = new byte[data.length-3];
		System.arraycopy(data, 3, nd, 0, nd.length);
		return new String(nd);
	}
	
	public static boolean isBOMFormat(byte data[]) {
		if(data[0] == (byte)0xEF && data[1] == (byte)0xBB && data[2] == (byte)0xBF) {
			return true;
		}
		return false;
	}
	
	public static void main(String args[]) {
		ConvertBOMFile c = new ConvertBOMFile();
		c.convert();
	}
}
