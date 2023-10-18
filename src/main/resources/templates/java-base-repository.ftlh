package ${model.packageName()};

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ${model.className()}<T> extends JpaRepository<T, ${model.keyClassName()}> {

}
<#-- @ftlvariable name="model" type="ru.classbase.repositoryprocessor.model.BaseRepositoryClass" -->