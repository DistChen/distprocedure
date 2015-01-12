package dist.common.procedure.define;

import java.util.Map;

/**
 * Created by dist on 14-12-30.
 */
public class ProcedureRepository {
    private static Map<String,ProcedureModel> procedures;

    private static Map<String,Map<String,ProcedureModel>> groups;

    public static Map<String, Map<String, ProcedureModel>> getGroups() {
        return groups;
    }

    public static void setGroups(Map<String, Map<String, ProcedureModel>> groups) {
        ProcedureRepository.groups = groups;
    }

    public static Map<String, ProcedureModel> getProcedures() {
        return procedures;
    }

    public static void setProcedures(Map<String, ProcedureModel> procedures) {
        ProcedureRepository.procedures = procedures;
    }

    public static ProcedureModel getProcedure(String id){
        return ProcedureRepository.procedures.get(id);
    }
    public static Map<String,ProcedureModel> getGroup(String groupID){
        return ProcedureRepository.groups.get(groupID);
    }
}