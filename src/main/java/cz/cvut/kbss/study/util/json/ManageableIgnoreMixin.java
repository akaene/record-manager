package cz.cvut.kbss.study.util.json;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * Mixin declared to allow ignoring JOPA's Manageable reference to the current UnitOfWork
 */
@JsonIgnoreType
public class ManageableIgnoreMixin {
}
